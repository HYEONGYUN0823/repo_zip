package com.a7a7.modeule.cart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usr/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/CartUsrList")
    public String showCheckoutPage(
            @RequestParam(name = "seq", required = false) String productSeqForDirectBuy, // 이름 변경 및 선택적 처리
            Model model,
            HttpSession session) {
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");

        List<CartItemDto> cartItems = cartService.getCartItems(session);
        int totalAmount = cartService.getCartTotalAmount(session);
        long cartItemCount = cartService.getCartItemCount(session);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("cartItemCount", cartItemCount);

        if (cartItems != null && !cartItems.isEmpty()) {
            session.setAttribute("orderItemListFromCart", cartItems);
        } else {
            session.removeAttribute("orderItemListFromCart");
        }
        return "usr/cart/CartUsrList";
    }

    @PostMapping("/addItem")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addItemToCartApi(
            @RequestParam(name = "productSeq") String productSeq, // ★★★ 수정: name 속성 추가 ★★★
            @RequestParam(name = "quantity", defaultValue = "1") int quantity, // ★★★ 수정: name 속성 추가 ★★★
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        if (userUiSeq == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            CartItemDto addedItem = cartService.addItemToCart(session, productSeq, quantity);
            response.put("success", true);
            response.put("message", "상품이 장바구니에 추가되었습니다.");
            response.put("item", addedItem);
            response.put("cartItemCount", cartService.getCartItemCount(session));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "장바구니 추가 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateQuantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateItemQuantityApi(
            @RequestParam(name = "productSeq") String productSeq, // ★★★ 수정: name 속성 추가 ★★★
            @RequestParam(name = "quantity") int quantity,       // ★★★ 수정: name 속성 추가 ★★★
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        if (userUiSeq == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if (quantity < 0) {
             response.put("success", false);
             response.put("message", "수량은 0 이상이어야 합니다.");
             return ResponseEntity.badRequest().body(response);
        }

        try {
            CartItemDto updatedItem = cartService.updateItemQuantity(session, productSeq, quantity);
            response.put("success", true);
            if (updatedItem != null) {
                response.put("message", "상품 수량이 변경되었습니다.");
                response.put("item", updatedItem);
            } else {
                 response.put("message", "상품이 장바구니에서 삭제되었습니다.");
            }
            response.put("cartItemCount", cartService.getCartItemCount(session));
            response.put("totalAmount", cartService.getCartTotalAmount(session));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "수량 변경 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/removeItem")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeItemFromCartApi(
            @RequestParam(name = "productSeq") String productSeq, // ★★★ 수정: name 속성 추가 ★★★
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        if (userUiSeq == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            cartService.removeItemFromCart(session, productSeq);
            response.put("success", true);
            response.put("message", "상품이 장바구니에서 삭제되었습니다.");
            response.put("cartItemCount", cartService.getCartItemCount(session));
            response.put("totalAmount", cartService.getCartTotalAmount(session));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // clearCartApi와 getCartCountApi는 요청 파라미터가 없으므로 @RequestParam 관련 문제는 발생하지 않습니다.
    // 하지만 일관성을 위해, 만약 이 메소드들이 나중에 파라미터를 받게 된다면 동일하게 name 속성을 명시해주는 것이 좋습니다.

    @PostMapping("/clearCart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCartApi(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        if (userUiSeq == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            cartService.clearCart(session);
            response.put("success", true);
            response.put("message", "장바구니가 비워졌습니다.");
            response.put("cartItemCount", 0L);
            response.put("totalAmount", 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "장바구니 비우기 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getCartCount")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartCountApi(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        if (userUiSeq == null) {
            response.put("count", 0L);
            return ResponseEntity.ok(response);
        }
        response.put("count", cartService.getCartItemCount(session));
        return ResponseEntity.ok(response);
    }

    // 오프캔버스 장바구니 요약 정보 API (이전 답변에서 JavaScript가 호출하도록 제안한 API)
    @GetMapping("/getCartSummary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartSummary(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String userUiSeq = (String) session.getAttribute("sessSeqUsr");

        if (userUiSeq == null) {
            response.put("cartItems", new ArrayList<>());
            response.put("totalAmount", 0);
            response.put("cartItemCount", 0L);
            return ResponseEntity.ok(response);
        }

        List<CartItemDto> cartItems = cartService.getCartItems(session);
        int totalAmount = cartService.getCartTotalAmount(session);
        long cartItemCount = cartService.getCartItemCount(session);

        response.put("cartItems", cartItems);
        response.put("totalAmount", totalAmount);
        response.put("cartItemCount", cartItemCount);
        return ResponseEntity.ok(response);
    }
}