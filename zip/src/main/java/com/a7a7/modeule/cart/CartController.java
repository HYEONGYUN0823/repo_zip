package com.a7a7.modeule.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    // 장바구니에 상품 추가
    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestBody CartDto cartDto, HttpSession session) {
        // 로그인 여부 체크
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) {
            return "not_logged_in";  // 로그인하지 않았으면 처리하지 않음
        }

        // 장바구니에 상품 추가
        cartService.addToCart(session, cartDto); 

        return "ok";  // 성공 메시지
    }

    // 장바구니 조회
    @GetMapping("/usr/cart/CartUsrList")
    public String viewCart(HttpSession session, Model model) {
        List<CartDto> cart = cartService.getCart(session);  // 세션에서 장바구니 가져오기
        model.addAttribute("cart", cart);  // 장바구니 데이터 모델에 추가
        return "usr/cart/CartUsrList";  // 장바구니 목록 페이지로 이동
    }
}