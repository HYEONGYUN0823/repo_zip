package com.a7a7.modeule.checkout;

import com.a7a7.modeule.member.MemberDto;         // MemberDto 실제 경로로 수정 필요
import com.a7a7.modeule.member.MemberService;   // MemberService 실제 경로로 수정 필요
import com.a7a7.modeule.order.OrderDto;       // OrderDto 실제 경로로 수정 필요
import com.a7a7.modeule.product.ProductDto;     // ProductDto 실제 경로로 수정 필요
import com.a7a7.modeule.product.ProductService; // ProductService 실제 경로로 수정 필요
import com.a7a7.modeule.upload.UploadDto;       // UploadDto 실제 경로로 수정 필요
import com.a7a7.modeule.upload.UploadService;   // UploadService 실제 경로로 수정 필요
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private MemberService memberService; // final 키워드 사용 고려 (생성자 주입 시)

    @Autowired
    private ProductService productService;

    @Autowired
    private UploadService uploadService;

    @GetMapping("/usr/checkout/CheckoutUsrList")
    public String prepareCheckoutPage(
            @RequestParam("seq") String productSeq,
            @RequestParam("quantity") int quantity,
            Model model,
            HttpSession session) {

        System.out.println("### CheckoutController.prepareCheckoutPage - Product Seq: " + productSeq + ", Quantity: " + quantity);

        // 1. 상품 상세 정보 조회
        ProductDto productParamDto = new ProductDto(); // DTO 객체명에 Dto 접미사 사용 권장
        productParamDto.setSeq(productSeq);
        ProductDto productDetail;
        try {
            productDetail = productService.selectOne(productParamDto);
        } catch (Exception e) {
            System.err.println("### CheckoutController.prepareCheckoutPage - 상품 조회 오류: " + e.getMessage());
            model.addAttribute("errorMessage", "상품 정보를 가져오는 중 오류가 발생했습니다.");
            return "usr/common/error_page"; // 실제 에러 페이지 경로
        }

        if (productDetail == null) {
            System.err.println("### CheckoutController.prepareCheckoutPage - 상품 정보 없음. Product Seq: " + productSeq);
            model.addAttribute("errorMessage", "요청 상품을 찾을 수 없습니다.");
            return "redirect:/usr/product/list";
        }

        // 2. 상품 대표 이미지 조회
        UploadDto imageParamDto = new UploadDto();
        imageParamDto.setPseq(productSeq);
        UploadDto mainProductImage = null;
        try {
            mainProductImage = uploadService.selectOne(imageParamDto);
            if (mainProductImage != null) {
                System.out.println("### CheckoutController.prepareCheckoutPage - 상품 이미지 조회 성공. Path: " + mainProductImage.getPath());
            } else {
                System.out.println("### CheckoutController.prepareCheckoutPage - 상품 이미지 없음. pseq: " + productSeq);
            }
        } catch (Exception e) {
            System.err.println("### CheckoutController.prepareCheckoutPage - 상품 이미지 조회 오류: " + e.getMessage());
        }

        // 3. 로그인한 사용자 정보 조회
        String loggedInUserSeq = (String) session.getAttribute("sessSeqUsr");
        MemberDto loggedInUser = null;
        if (loggedInUserSeq != null) {
            MemberDto userParamDto = new MemberDto();
            userParamDto.setSeq(loggedInUserSeq);
            try {
                loggedInUser = memberService.selectOne(userParamDto);
            } catch (Exception e) {
                System.err.println("### CheckoutController.prepareCheckoutPage - 사용자 정보 조회 오류: " + e.getMessage());
            }
        }
        model.addAttribute("user", loggedInUser);


        // ★★★ 4. 주문할 아이템 정보를 OrderDto 객체로 만들고 세션에 저장 ★★★
        OrderDto orderItem = new OrderDto();
        orderItem.setMealKitSeq(productDetail.getSeq()); // 상품의 실제 PK

        // ProductDto에 상품명, 가격을 가져오는 getter가 있다고 가정
        // 실제 ProductDto의 필드명/메소드명에 맞게 수정 필요
        if (productDetail.getMealKitName() != null) { // Null 체크 추가
            orderItem.setProductName(productDetail.getMealKitName());
        } else {
            orderItem.setProductName("알 수 없는 상품"); // 기본값 설정
            System.err.println("### CheckoutController.prepareCheckoutPage - ProductDto에 상품명이 없습니다. Product Seq: " + productSeq);
        }

        orderItem.setQuantity(quantity);

        if (productDetail.getPrice() != null) { // Null 체크 추가
            orderItem.setPrice(productDetail.getPrice());
        } else {
            orderItem.setPrice(0); // 기본값 또는 오류 처리
            System.err.println("### CheckoutController.prepareCheckoutPage - ProductDto에 가격 정보가 없습니다. Product Seq: " + productSeq);
        }
        // if (mainProductImage != null && mainProductImage.getPath() != null) {
        //    orderItem.setProductImageUrl(mainProductImage.getPath()); // OrderDto에 이미지 경로 필드가 있다면
        // }

        List<OrderDto> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        session.setAttribute("orderItemList", orderItemList);
        System.out.println("### CheckoutController.prepareCheckoutPage - 세션 'orderItemList' 저장 완료. 항목 수: " + orderItemList.size());
        if (!orderItemList.isEmpty()) {
            OrderDto firstItem = orderItemList.get(0);
            System.out.println("    - 저장된 첫 아이템: mealKitSeq=" + firstItem.getMealKitSeq() +
                               ", productName=" + firstItem.getProductName() +
                               ", quantity=" + firstItem.getQuantity() +
                               ", price=" + firstItem.getPrice());
        }


        // 5. 결제 준비 페이지(Checkout.html)에 필요한 정보들을 Model에 담아 전달
        model.addAttribute("product", productDetail);
        model.addAttribute("productImage", mainProductImage);
        model.addAttribute("quantity", quantity);

        int calculatedTotalPrice = (productDetail.getPrice() != null ? productDetail.getPrice() : 0) * quantity;
        model.addAttribute("totalPrice", calculatedTotalPrice);
        model.addAttribute("orderItemsForDisplay", orderItemList); // 화면 주문 요약 표시용

        // 이 뷰는 usr/checkout/Checkout.html (또는 유사한 경로)를 가리킵니다.
        // 이 HTML에는 Toss Payments SDK 연동 JavaScript가 포함되어야 합니다.
        return "usr/checkout/Checkout";
    }
}