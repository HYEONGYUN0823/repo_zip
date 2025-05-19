package com.a7a7.modeule.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.modeule.member.MemberDto;
import com.a7a7.modeule.member.MemberService;
import com.a7a7.modeule.product.ProductDto;
import com.a7a7.modeule.product.ProductService;
import com.a7a7.modeule.upload.UploadDto;
import com.a7a7.modeule.upload.UploadService;

import jakarta.servlet.http.HttpSession;


@Controller
public class CheckoutController {
    @Autowired
    MemberService memberService;
    @Autowired
    ProductService productService;
    @Autowired
    UploadService uploadService;

    @GetMapping("/usr/checkout/CheckoutUsrList")
    public String checkoutUsrList(@RequestParam("seq") String seq,  // 상품 seq만 받아옴
                                  @RequestParam("quantity") int quantity,
                                  Model model, HttpSession session) throws Exception {

        // 1. 상품 상세 조회
        ProductDto product = new ProductDto();
        product.setSeq(seq);
        ProductDto productDetail = productService.selectOne(product);

        // 2. 상품 이미지 조회
        UploadDto uploadDto = new UploadDto();
        uploadDto.setPseq(seq);
        UploadDto itemFile = uploadService.selectOne(uploadDto);

        // 3. 유저 정보 조회 (생략 가능)
        String sessSeqUsr = (String) session.getAttribute("sessSeqUsr");
        if (sessSeqUsr != null) {
            MemberDto memberDto = new MemberDto();
            memberDto.setSeq(sessSeqUsr);
            MemberDto user = memberService.selectOne(memberDto);
            model.addAttribute("user", user);
        }

        // 4. 모델에 담기
        model.addAttribute("product", productDetail);
        model.addAttribute("productImage", itemFile);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", productDetail.getPrice() * quantity);

        return "usr/checkout/Checkout";
    }
	
}
