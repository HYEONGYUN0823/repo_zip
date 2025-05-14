package com.a7a7.modeule.cart;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {
	
	private static final String CART_SESSION_KEY = "cart";
	
    // 장바구니에 상품 추가
    public void addToCart(HttpSession session, CartDto item) {
        List<CartDto> cart = (List<CartDto>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        cart.add(item);
        session.setAttribute(CART_SESSION_KEY, cart);  // 세션에 장바구니 다시 저장
    }

    // 장바구니에서 상품 제거
    public void removeFromCart(HttpSession session, String productSeq) {  // productId -> productSeq로 변경
        List<CartDto> cart = (List<CartDto>) session.getAttribute(CART_SESSION_KEY);
        if (cart != null) {
            cart.removeIf(item -> item.getProductSeq().equals(productSeq));  // 해당 상품 제거
            session.setAttribute(CART_SESSION_KEY, cart);  // 세션에 장바구니 다시 저장
        }
    }

    // 장바구니 조회
    public List<CartDto> getCart(HttpSession session) {
        List<CartDto> cart = (List<CartDto>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }
}