package com.a7a7.modeule.checkout;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.modeule.cart.CartItemDto;
import com.a7a7.modeule.code.CodeDto;         // CodeDto 임포트
import com.a7a7.modeule.code.CodeService;     // CodeService 임포트
import com.a7a7.modeule.member.MemberDto;
import com.a7a7.modeule.member.MemberService;
import com.a7a7.modeule.order.OrderDto;
import com.a7a7.modeule.product.ProductDto;
import com.a7a7.modeule.product.ProductService;
import com.a7a7.modeule.upload.UploadDto;
import com.a7a7.modeule.upload.UploadService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Value("${tossPay_cli_api}")
    private String tossPaymentsClientKey;

    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private CodeService codeService; // ★★★ CodeService 주입 ★★★

    @GetMapping("/usr/checkout/CheckoutUsrList")
    public String prepareCheckoutPage(
            @RequestParam(name = "seq", required = false) String productSeq,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            Model model,
            HttpSession session) {

        model.addAttribute("tossPaymentsClientKeyForJs", this.tossPaymentsClientKey);
        List<OrderDto> orderItemsToProcess = new ArrayList<>();
        int calculatedTotalPrice = 0;
        String loggedInUserSeq = (String) session.getAttribute("sessSeqUsr");

        if (loggedInUserSeq == null) { /* ... (로그인 리다이렉트) ... */
            String redirectParams = "";
            if (productSeq != null && quantity != null) {
                redirectParams = "?seq=" + productSeq + "&quantity=" + quantity;
            }
            return "redirect:/usr/member/loginUsrForm?redirectURL=/usr/checkout/CheckoutUsrList" + redirectParams;
        }

        MemberDto loggedInUser = null;
        MemberDto userParamDto = new MemberDto();
        userParamDto.setSeq(loggedInUserSeq);
        try { loggedInUser = memberService.selectOne(userParamDto); }
        catch (Exception e) { model.addAttribute("errorMessage", "사용자 정보 조회 오류"); return "usr/common/error_page"; }
        model.addAttribute("user", loggedInUser);

        if (productSeq != null && quantity != null && quantity > 0) {
            // --- 바로 구매 시나리오 ---
            System.out.println("### CheckoutController - 바로 구매 시나리오");
            ProductDto productParamDto = new ProductDto();
            productParamDto.setSeq(productSeq);
            ProductDto productDetail;
            try { productDetail = productService.selectOne(productParamDto); }
            catch (Exception e) { model.addAttribute("errorMessage", "상품 조회 오류"); return "usr/common/error_page"; }

            if (productDetail == null) { /* ... 상품 없음 처리 ... */ }
            if (productDetail.getStock() == null || productDetail.getStock() < quantity) { /* ... 재고 부족 처리 ... */ }

            UploadDto imageParamDto = new UploadDto();
            imageParamDto.setPseq(productSeq);
            UploadDto mainProductImage = null;
            try { mainProductImage = uploadService.selectOne(imageParamDto); }
            catch (Exception e) { System.err.println("이미지 조회 오류 (바로 구매): " + e.getMessage());}

            // ★★★ 바로 구매 시 브랜드명 조회 ★★★
            String brandNameToDisplayDirect = "브랜드 없음";
            if (productDetail.getBrandName() != 0) {
                try {
                    List<CodeDto> brandCodes = codeService.selectListCachedCode("3"); // 브랜드 코드 그룹 "3" 가정
                    for (CodeDto code : brandCodes) {
                        if (String.valueOf(productDetail.getBrandName()).equals(code.getIfcdSeq())) {
                            brandNameToDisplayDirect = code.getIfcdName();
                            break;
                        }
                    }
                } catch (Exception e) { System.err.println("CheckoutController: 브랜드명 조회 오류(바로구매) - " + e.getMessage());}
            }

            OrderDto orderItem = new OrderDto();
            orderItem.setMealKitSeq(productDetail.getSeq());
            orderItem.setProductName(productDetail.getMealKitName() != null ? productDetail.getMealKitName() : "상품명 없음");
            orderItem.setQuantity(quantity);
            orderItem.setPrice(productDetail.getPrice() != null ? productDetail.getPrice() : 0);
            if (mainProductImage != null && mainProductImage.getPath() != null) {
                orderItem.setProductImageUrl(mainProductImage.getPath());
            } else {
                orderItem.setProductImageUrl("/user/template/user_ui/assets/images/products/product-img-default.jpg");
            }
            orderItem.setBrandNameDisplay(brandNameToDisplayDirect); // ★★★ 브랜드명 설정 ★★★
            orderItemsToProcess.add(orderItem);
            calculatedTotalPrice = (productDetail.getPrice() != null ? productDetail.getPrice() : 0) * quantity;

        } else {
            // --- 장바구니 전체 결제 시나리오 ---
            System.out.println("### CheckoutController - 장바구니 결제 시나리오");
            @SuppressWarnings("unchecked")
            List<CartItemDto> cartItemsFromSession = (List<CartItemDto>) session.getAttribute("orderItemListFromCart");

            if (cartItemsFromSession == null || cartItemsFromSession.isEmpty()) {
                return "redirect:/usr/cart/CartUsrList?message=emptyCart";
            }

            for (CartItemDto cartItem : cartItemsFromSession) {
                // ... (재고 확인 로직은 이전과 동일) ...
                ProductDto productParam = new ProductDto();
                productParam.setSeq(cartItem.getProductSeq());
                ProductDto currentProductInfo = null;
                try { currentProductInfo = productService.selectOne(productParam); } catch (Exception e) { /* log */ }
                if (currentProductInfo == null || currentProductInfo.getStock() == null || currentProductInfo.getStock() < cartItem.getQuantity()) {
                    String productName = currentProductInfo != null ? currentProductInfo.getMealKitName() : cartItem.getProductName();
                    return "redirect:/usr/cart/CartUsrList?message=stockNotEnough&productName=" + java.net.URLEncoder.encode(productName, java.nio.charset.StandardCharsets.UTF_8);
                }

                OrderDto orderItem = new OrderDto();
                orderItem.setMealKitSeq(cartItem.getProductSeq());
                orderItem.setProductName(cartItem.getProductName());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                orderItem.setProductImageUrl(cartItem.getProductImageUrl());
                orderItem.setBrandNameDisplay(cartItem.getBrandNameDisplay()); // ★★★ CartItemDto의 브랜드명 사용 ★★★
                orderItemsToProcess.add(orderItem);
                calculatedTotalPrice += (cartItem.getPrice() != null ? cartItem.getPrice() : 0) * (cartItem.getQuantity() != null ? cartItem.getQuantity() : 0);
            }
        }

        if (orderItemsToProcess.isEmpty()) {
            return "redirect:/usr/cart/CartUsrList?message=noItemsToOrder";
        }
        session.setAttribute("orderItemList", orderItemsToProcess); // PaymentController에서 사용할 주문 목록

        model.addAttribute("orderItemsForDisplay", orderItemsToProcess);
        model.addAttribute("totalPrice", calculatedTotalPrice);

        String clientOrderId = "order_" + (loggedInUserSeq != null ? loggedInUserSeq.replaceAll("[^a-zA-Z0-9]", "") : "GUEST")
                             + "_" + System.currentTimeMillis();
        model.addAttribute("clientOrderId", clientOrderId);

        return "usr/checkout/Checkout";
    }
}