package com.a7a7.modeule.order;

public class OrderDto {

    // orderItem 정보 (주문 생성 시 orderItemList의 각 아이템에 사용)
    private String productName; // 상품명 (조회 시 사용)
    private Integer quantity;
    private Integer price;      // 주문 아이템의 단가
    private String mealKitSeq;  // ★★★ mealKit 테이블의 PK를 참조하는 ID (orderItem.mealKit_seq 컬럼에 저장될 값) ★★★
    private String brandNameDisplay;

    // order 테이블 정보
    private String seq;         // ★ order 테이블의 PK (DB 자동 생성, 조회 시 주문 대표 번호로 사용 가능)
    private String userUiSeq;   // 주문자 ID (userUi 테이블의 PK)
    private Integer total_price; // 주문 총액
    private String status;      // 주문 상태
    private String orderDate;   // 주문 날짜 (문자열 or Date)
    
    private String productImageUrl;

    // TossPayments 연동 정보
    private String orderId;     // TossPayments의 주문 ID (DB order 테이블에도 저장 가능)

    // 기본 생성자
    public OrderDto() {}

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getMealKitSeq() { return mealKitSeq; }
    public void setMealKitSeq(String mealKitSeq) { this.mealKitSeq = mealKitSeq; }

    public String getSeq() { return seq; }
    public void setSeq(String seq) { this.seq = seq; }

    public String getUserUiSeq() { return userUiSeq; }
    public void setUserUiSeq(String userUiSeq) { this.userUiSeq = userUiSeq; }

    public Integer getTotal_price() { return total_price; }
    public void setTotal_price(Integer total_price) { this.total_price = total_price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

	public String getProductImageUrl() {
		return productImageUrl;
	}

	public void setProductImageUrl(String productImageUrl) {
		this.productImageUrl = productImageUrl;
	}

	public String getBrandNameDisplay() {
		return brandNameDisplay;
	}

	public void setBrandNameDisplay(String brandNameDisplay) {
		this.brandNameDisplay = brandNameDisplay;
	}
    
    
}