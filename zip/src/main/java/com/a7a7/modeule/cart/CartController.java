package com.a7a7.modeule.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller  // @Controller는 그대로 사용
public class CartController {

    @Autowired
    CartService cartService;

    // 장바구니에 상품 추가
    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam String productId, @RequestParam int quantity,
                            @RequestParam int price, HttpSession session) {
        CartDto item = new CartDto(productId, quantity, price);
        cartService.addToCart(session, item);  // 서비스 호출
        return "ok";  // 응답 본문에 "ok" 문자열을 반환
    }

    // 장바구니에서 상품 제거
    @PostMapping("/cart/remove")
    @ResponseBody
    public String removeFromCart(@RequestParam String productId, HttpSession session) {
        cartService.removeFromCart(session, productId);  // 서비스 호출
        return "ok";  // 응답 본문에 "ok" 문자열을 반환
    }

    // 장바구니 조회
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartDto> cart = cartService.getCart(session);  // 서비스 호출
        model.addAttribute("cart", cart);  // 장바구니 목록을 뷰에 전달
        return "cart/viewCart";  // 장바구니 페이지로 이동
    }
}