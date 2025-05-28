package com.a7a7.modeule.cart;

import com.a7a7.modeule.code.CodeDto; // CodeDto 또는 CodeGroupDto (실제 사용하는 DTO로)
import com.a7a7.modeule.code.CodeService;   // CodeService 주입
import com.a7a7.modeule.product.ProductDto;
import com.a7a7.modeule.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CodeService codeService; // ★★★ CodeService 주입 ★★★

    private static final String CART_SESSION_KEY = "sessionUserCart_a7a7";

    @SuppressWarnings("unchecked")
    private List<CartItemDto> getCartFromSession(HttpSession session) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public CartItemDto addItemToCart(HttpSession session, String productSeq, int quantity) {
        List<CartItemDto> cart = getCartFromSession(session);
        ProductDto productParam = new ProductDto();
        productParam.setSeq(productSeq);
        ProductDto product = productService.selectOne(productParam);

        if (product == null) throw new IllegalArgumentException("상품 정보를 찾을 수 없습니다: " + productSeq);
        if (product.getStock() == null) throw new IllegalStateException("상품 재고 정보를 확인할 수 없습니다: " + product.getMealKitName());
        if (product.getStock() <= 0) throw new IllegalStateException("상품이 품절되었습니다: " + product.getMealKitName());
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");

        // ★★★ 브랜드명 조회 로직 시작 ★★★
        String brandNameToDisplay = "브랜드 없음"; // 기본값
        if (product.getBrandName() != 0) { // ProductDto의 brandName이 int 타입 코드값이라고 가정
            try {
                // CodeService를 사용하여 브랜드 코드에 해당하는 이름을 가져옵니다.
                // CodeService에 groupSeq와 detailCode(여기서는 product.getBrandName())로 이름을 찾는 메소드가 있다고 가정합니다.
                // 예: CodeDto brandCode = codeService.selectOneCode(3, String.valueOf(product.getBrandName()));
                // 또는 CodeService.selectListCachedCode(그룹코드) 후 필터링
                List<CodeDto> brandCodes = codeService.selectListCachedCode("3"); // 브랜드 코드 그룹이 "3"이라고 가정
                for (CodeDto code : brandCodes) {
                    if (String.valueOf(product.getBrandName()).equals(code.getIfcdSeq())) { // CodeDto의 코드값 필드가 seq라고 가정
                        brandNameToDisplay = code.getIfcdName(); // CodeDto의 코드명 필드가 name이라고 가정
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("CartService: 브랜드명 조회 중 오류 발생 - " + e.getMessage());
                // 오류 발생 시 기본값 사용
            }
        }
        // ★★★ 브랜드명 조회 로직 끝 ★★★

        Optional<CartItemDto> existingItemOpt = cart.stream()
            .filter(item -> item.getProductSeq().equals(productSeq))
            .findFirst();

        CartItemDto cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > product.getStock()) throw new IllegalStateException("재고 부족");
            cartItem.setQuantity(newQuantity);
            // 이미 담긴 상품의 브랜드명은 업데이트하지 않거나, 필요시 product.getPath()와 brandNameToDisplay도 업데이트
            cartItem.setProductImageUrl(product.getPath()); // 이미지 URL도 업데이트 (가격처럼)
            cartItem.setBrandNameDisplay(brandNameToDisplay); // 브랜드명도 업데이트
        } else {
            if (quantity > product.getStock()) throw new IllegalStateException("재고 부족");
            cartItem = new CartItemDto(
                    product.getSeq(),
                    product.getMealKitName(),
                    product.getPrice(),
                    quantity,
                    product.getPath(), // ProductDto의 이미지 경로
                    brandNameToDisplay  // 조회된 브랜드명
            );
            cart.add(cartItem);
        }

        cartItem.setPrice(product.getPrice());
        session.setAttribute(CART_SESSION_KEY, cart);
        return cartItem;
    }

    public CartItemDto updateItemQuantity(HttpSession session, String productSeq, int newQuantity) {
        List<CartItemDto> cart = getCartFromSession(session);
        ProductDto productParam = new ProductDto();
        productParam.setSeq(productSeq);
        ProductDto product = productService.selectOne(productParam);

        if (product == null) throw new IllegalArgumentException("상품 정보 없음: " + productSeq);
        if (product.getStock() == null) throw new IllegalStateException("재고 정보 없음: " + product.getMealKitName());

        Optional<CartItemDto> itemToUpdateOpt = cart.stream()
            .filter(item -> item.getProductSeq().equals(productSeq))
            .findFirst();

        if (itemToUpdateOpt.isEmpty()) throw new IllegalArgumentException("장바구니에 없는 상품: " + productSeq);

        CartItemDto cartItem = itemToUpdateOpt.get();
        if (newQuantity <= 0) {
            cart.remove(cartItem);
            session.setAttribute(CART_SESSION_KEY, cart);
            return null;
        }
        if (newQuantity > product.getStock()) throw new IllegalStateException("재고 부족");

        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(product.getPrice());
        // 수량 변경 시 이미지나 브랜드명은 변경되지 않으므로 그대로 둡니다.
        session.setAttribute(CART_SESSION_KEY, cart);
        return cartItem;
    }

    public void removeItemFromCart(HttpSession session, String productSeq) {
        List<CartItemDto> cart = getCartFromSession(session);
        cart.removeIf(item -> item.getProductSeq().equals(productSeq));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public List<CartItemDto> getCartItems(HttpSession session) {
        return getCartFromSession(session);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public Long getCartItemCount(HttpSession session) {
        return (long) getCartFromSession(session).size();
    }

    public int getCartTotalAmount(HttpSession session) {
        return getCartFromSession(session).stream()
            .mapToInt(item -> (item.getPrice() != null ? item.getPrice() : 0) * (item.getQuantity() != null ? item.getQuantity() : 0))
            .sum();
    }
}