package com.a7a7.modeule.cart;

import java.io.Serializable;

public class CartItemDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productSeq;      // 상품 ID (Product 테이블의 PK)
    private String productName;     // 상품명
    private Integer price;          // 상품 단가
    private Integer quantity;       // 담은 수량
    private String productImageUrl; // 상품 이미지 URL (선택적)
    private String brandNameDisplay;

    // 기본 생성자
    public CartItemDto() {
    }

    // 모든 필드를 받는 생성자
    public CartItemDto(String productSeq, String productName, Integer price, Integer quantity, String productImageUrl, String brandNameDisplay) {
        this.productSeq = productSeq;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.productImageUrl = productImageUrl;
        this.brandNameDisplay = brandNameDisplay;
    }

    // Getters and Setters
    public String getProductSeq() {
        return productSeq;
    }

    public void setProductSeq(String productSeq) {
        this.productSeq = productSeq;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Integer getTotalPrice() {
        if (this.price != null && this.quantity != null) {
            return this.price * this.quantity;
        }
        return 0;
    }
    
    public String getBrandNameDisplay() {
		return brandNameDisplay;
	}

	public void setBrandNameDisplay(String brandNameDisplay) {
		this.brandNameDisplay = brandNameDisplay;
	}

	@Override
    public String toString() {
        return "CartItemDto{" +
                "productSeq='" + productSeq + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", productImageUrl='" + productImageUrl + '\'' +
                '}';
    }
}