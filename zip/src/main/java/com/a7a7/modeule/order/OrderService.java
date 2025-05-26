package com.a7a7.modeule.order;

import java.util.List;

public interface OrderService {
    void createOrder(OrderDto orderDto, List<OrderDto> orderItems);
    List<OrderDto> getOrderListByUser(String userUiSeq);
    List<OrderDto> getOrdersByUserSeq(String userUiSeq);
}
