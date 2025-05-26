package com.a7a7.modeule.order;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderDao {
    int insertOrder(OrderDto orderDto);         // 주문 테이블
    int insertOrderItem(OrderDto orderDto);     // 주문 아이템 테이블

    List<OrderDto> selectOrderListByUser(String userUiSeq); // 유저별 주문 내역 조회
}
