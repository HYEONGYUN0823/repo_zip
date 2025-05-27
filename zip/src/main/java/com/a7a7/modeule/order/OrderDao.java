package com.a7a7.modeule.order;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao {
    int insertOrder(OrderDto orderDto);
    int insertOrderItem(OrderDto orderItemDto);
    List<OrderDto> getOrdersByUserSeq(@Param("userUiSeq") String userUiSeq);
    // List<OrderDto> selectOrderListByUser(@Param("userUiSeq") String userUiSeq); // 필요하다면 이것도 유지
}
