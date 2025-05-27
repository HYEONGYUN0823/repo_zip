package com.a7a7.modeule.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Transactional
    public void createOrder(OrderDto orderMasterDto, List<OrderDto> orderItems) {
        // 1. order 테이블에 주문 마스터 정보 저장
        orderDao.insertOrder(orderMasterDto); // 이 호출 후 orderMasterDto.getSeq()에 생성된 order 테이블 PK가 채워짐
        String generatedOrderMasterSeq = orderMasterDto.getSeq(); // 명확한 변수명 사용

        // 2. orderItem 테이블에 주문 상세 아이템들 저장
        for (OrderDto item : orderItems) {
            // OrderDto 객체를 재활용하지만, orderItem 테이블에 필요한 정보만 사용됨.
            // item.setSeq(generatedOrderMasterSeq); // 이렇게 하면 item의 seq가 order 마스터 seq로 덮어쓰여 혼란 야기 가능
                                                  // 대신, insertOrderItem 쿼리에서 order_seq 파라미터를 별도로 받거나,
                                                  // OrderDto에 orderMasterSeq 같은 임시 필드를 두고 활용할 수 있음.
                                                  // 여기서는 item 객체에 이미 mealKitSeq, quantity, price가 있고,
                                                  // XML에서 order_seq를 #{masterSeqParam} 등으로 받고,
                                                  // 호출 시 orderDao.insertOrderItem(item, generatedOrderMasterSeq) 처럼 할 수도 있음.
                                                  // 더 간단하게는, OrderDto에 'orderMasterSeqForOrderItem' 같은 필드를 추가하고 여기에 세팅 후,
                                                  // XML에서 #{orderMasterSeqForOrderItem} 으로 참조.

            // 가장 일반적인 방법: OrderItemDto를 별도로 만들거나, OrderDto에 임시 필드 활용
            // 여기서는 OrderDto를 그대로 쓰되, XML에서 파라미터 이름을 명확히 구분하는 방식을 택한다고 가정.
            // OrderDto에 order_seq로 사용될 값을 임시로 seq 필드에 넣고, XML에서는 #{seq}를 order_seq로 사용
            OrderDto orderItemToInsert = new OrderDto();
            orderItemToInsert.setQuantity(item.getQuantity());
            orderItemToInsert.setPrice(item.getPrice());
            orderItemToInsert.setMealKitSeq(item.getMealKitSeq()); // 상품 ID
            orderItemToInsert.setSeq(generatedOrderMasterSeq);     // ★★★ 이 item의 order_seq가 될 값 ★★★

            orderDao.insertOrderItem(orderItemToInsert);
        }
    }

    public List<OrderDto> getOrdersByUserSeq(String userUiSeq) {
        return orderDao.getOrdersByUserSeq(userUiSeq);
    }
}