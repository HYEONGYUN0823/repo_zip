package com.a7a7.modeule.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public void createOrder(OrderDto orderDto, List<OrderDto> orderItems) {
        // 1. order 테이블에 먼저 저장
        orderDao.insertOrder(orderDto);

        // 2. orderItem 테이블에 여러 개 저장 (반복)
        for (OrderDto item : orderItems) {
            item.setSeq(orderDto.getSeq()); // 주문 번호 (order_seq)에 저장
            orderDao.insertOrderItem(item);
        }
    }

    @Override
    public List<OrderDto> getOrderListByUser(String userUiSeq) {
        return orderDao.selectOrderListByUser(userUiSeq);
    }
}
