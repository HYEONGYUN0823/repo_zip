package com.a7a7.modeule.order;

public class OrderDto {
	
    // orderItem에서 가져올 정보 (orderId는 토스페이 참조용. DB PK는 아님)
    private String orderId;
    private String productName;
    private Integer quantity;
    private Integer price;

    // order 테이블의 필드 (ERD에 맞게 데이터 타입 변경)
    private String seq;
    private String userUiSeq;
    private Integer total_price; 
    private String status;
    private String orderDate;


    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getOrderDate() { return orderDate; }
	public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
	
	public String getSeq() { return seq; }
    public void setSeq(String seq) { this.seq = seq; }

    public String getUserUiSeq() { return userUiSeq; }
    public void setUserUiSeq(String userUiSeq) { this.userUiSeq = userUiSeq; }

    public Integer getTotal_price() { return total_price; }
    public void setTotal_price(Integer total_price) { this.total_price = total_price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
	
}
