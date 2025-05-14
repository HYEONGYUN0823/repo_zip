package com.a7a7.modeule.cart;

public class CartDto {
	
	private String productSeq;  // productId -> productSeq로 변경
	private String productName;
	private int quantity;
	private int price;
    
//  -----  
	public String getProductSeq() {  // getter 메서드도 productSeq로 변경
		return productSeq;
	}
	public void setProductSeq(String productSeq) {  // setter 메서드도 productSeq로 변경
		this.productSeq = productSeq;
	}
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
}
