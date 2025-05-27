package com.a7a7.modeule.order; // ★★★ 실제 OrderService 패키지 경로 ★★★

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.a7a7.modeule.product.ProductService;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductService productService; // ★★★ ProductService 의존성 주입 ★★★

    /**
     * 주문을 생성하고, 주문 아이템을 저장하며, 관련된 상품의 재고를 감소시킵니다.
     * 이 모든 과정은 하나의 트랜잭션으로 처리됩니다.
     *
     * @param orderMasterDto 주문 마스터 정보 (userUiSeq, total_price, status, tossOrderId 등 포함)
     * @param orderItemsFromSession 주문할 상품 아이템 목록 (각 아이템에 mealKitSeq, quantity, price 포함)
     * @throws Exception 상품 재고 부족 또는 DB 저장 실패 등 오류 발생 시
     */
    @Transactional // 주문 생성, 아이템 저장, 재고 감소는 원자적으로 실행되어야 함
    public void createOrder(OrderDto orderMasterDto, List<OrderDto> orderItemsFromSession) throws Exception {
        System.out.println("### OrderService.createOrder - 시작. 주문자: " + orderMasterDto.getUserUiSeq() +
                           ", TossOrderID: " + orderMasterDto.getOrderId() +
                           ", 총액: " + orderMasterDto.getTotal_price());

        // 1. 주문 마스터 정보(order 테이블) 저장
        orderDao.insertOrder(orderMasterDto);
        // insertOrder 후, MyBatis의 <selectKey>에 의해 orderMasterDto.getSeq()에
        // 생성된 order 테이블의 PK (seq)가 채워져야 합니다.
        String generatedOrderMasterSeq = orderMasterDto.getSeq();
        System.out.println("### OrderService - 주문 마스터 생성 완료. DB PK (seq): " + generatedOrderMasterSeq);

        if (generatedOrderMasterSeq == null || generatedOrderMasterSeq.trim().isEmpty()) {
            System.err.println("### OrderService - 치명적 오류: 생성된 주문 마스터 PK(seq)를 가져오지 못했습니다! (MyBatis <selectKey> 설정 확인 필요)");
            throw new RuntimeException("주문 처리 중 시스템 오류가 발생했습니다. (주문 번호 생성 실패)");
        }

        // 2. 주문 아이템(orderItem 테이블) 저장 및 해당 상품 재고 감소
        if (orderItemsFromSession == null || orderItemsFromSession.isEmpty()) {
            System.err.println("### OrderService - 주문할 상품 아이템 목록(orderItemsFromSession)이 비어있거나 null입니다!");
            throw new IllegalArgumentException("주문할 상품이 없습니다."); // 잘못된 파라미터로 간주
        }

        System.out.println("### OrderService - 주문 아이템 처리 시작. 총 " + orderItemsFromSession.size() + " 종류의 아이템.");
        for (OrderDto itemFromSession : orderItemsFromSession) {
            // orderItem 테이블에 삽입할 OrderDto 객체 준비
            OrderDto orderItemToInsert = new OrderDto();
            orderItemToInsert.setQuantity(itemFromSession.getQuantity());
            orderItemToInsert.setPrice(itemFromSession.getPrice());         // 개별 아이템의 단가
            orderItemToInsert.setMealKitSeq(itemFromSession.getMealKitSeq()); // 상품 ID (mealKit 테이블의 PK)
            orderItemToInsert.setSeq(generatedOrderMasterSeq);              // 이 orderItem의 order_seq (방금 생성된 order 마스터 PK)

            // 주문 아이템 정보 유효성 검사 (필수 정보 누락 방지)
            if (orderItemToInsert.getMealKitSeq() == null || orderItemToInsert.getMealKitSeq().trim().isEmpty()) {
                String productNameInfo = itemFromSession.getProductName() != null ? itemFromSession.getProductName() : "알 수 없는 상품";
                System.err.println("### OrderService - 주문 아이템의 상품 ID(mealKitSeq)가 누락되었습니다. 세션 데이터 확인 필요. 상품명 추정: " + productNameInfo);
                throw new IllegalArgumentException("주문 상품 정보에 오류가 있습니다 (상품 ID 누락). 상품: " + productNameInfo);
            }
            if (orderItemToInsert.getQuantity() == null || orderItemToInsert.getQuantity() <= 0) {
                System.err.println("### OrderService - 주문 아이템의 수량이 유효하지 않습니다. 상품 ID: " + orderItemToInsert.getMealKitSeq() + ", 수량: " + itemFromSession.getQuantity());
                throw new IllegalArgumentException("주문 상품의 수량이 올바르지 않습니다. (상품 ID: " + orderItemToInsert.getMealKitSeq() + ")");
            }

            System.out.println("    - 주문 아이템 저장 시도: mealKitSeq=" + orderItemToInsert.getMealKitSeq() +
                               ", quantity=" + orderItemToInsert.getQuantity() +
                               ", price=" + orderItemToInsert.getPrice() +
                               ", order_seq (DB PK)=" + orderItemToInsert.getSeq());
            orderDao.insertOrderItem(orderItemToInsert);
            System.out.println("    - 주문 아이템 저장 완료 for mealKitSeq: " + orderItemToInsert.getMealKitSeq());

            // 3. 해당 상품 재고 감소
            try {
                System.out.println("    - 재고 감소 시도: 상품 ID(mealKitSeq)=" + orderItemToInsert.getMealKitSeq() + ", 감소 수량=" + orderItemToInsert.getQuantity());
                int updatedStockRows = productService.decreaseStock(orderItemToInsert.getMealKitSeq(), orderItemToInsert.getQuantity());

                if (updatedStockRows > 0) {
                    System.out.println("    - 재고 감소 성공. 상품 ID: " + orderItemToInsert.getMealKitSeq());
                } else {
                    // productService.decreaseStock 내부에서 재고 부족 시 예외를 던지도록 구현했다면,
                    // 이 else 블록은 productSeq가 DB에 없거나, DAO의 WHERE 조건(AND stock >= quantity)만으로 막힌 경우에 도달 가능.
                    // 어떤 경우든 재고 업데이트가 안 된 것은 심각한 문제이므로 롤백 유도.
                    System.err.println("### OrderService - 재고 감소가 적용되지 않았습니다 (0 rows updated). 상품 ID: " + orderItemToInsert.getMealKitSeq() + ". 주문을 롤백합니다.");
                    throw new RuntimeException("상품 재고를 업데이트하는데 실패했습니다. 주문이 취소됩니다. (상품 ID: " + orderItemToInsert.getMealKitSeq() + ")");
                }
            } catch (Exception e) { // ProductService.decreaseStock에서 던진 예외 (예: 재고 부족)
                System.err.println("### OrderService - 재고 감소 중 예외 발생: " + e.getMessage() + ". 상품 ID: " + orderItemToInsert.getMealKitSeq() + ". 주문 전체를 롤백합니다.");
                // 이미 @Transactional에 의해 롤백될 것이지만, 명확한 원인과 함께 예외를 다시 던져서
                // 호출한 쪽(PaymentController)에서 인지하고 사용자에게 적절한 메시지를 전달하도록 함.
                throw new RuntimeException("주문 처리 중 '" + itemFromSession.getProductName() + "' 상품의 재고 관련 문제가 발생했습니다: " + e.getMessage(), e);
            }
        }
        System.out.println("### OrderService.createOrder - 모든 주문 아이템 처리 및 재고 감소 완료. DB 주문 PK: " + generatedOrderMasterSeq);
    }

    public List<OrderDto> getOrdersByUserSeq(String userUiSeq) {
        System.out.println("### OrderService.getOrdersByUserSeq - 사용자 ID: " + userUiSeq);
        List<OrderDto> orders = orderDao.getOrdersByUserSeq(userUiSeq);
        if (orders != null) {
            System.out.println("### OrderService - 조회된 주문 관련 아이템 수: " + orders.size());
        } else {
            System.out.println("### OrderService - 조회된 주문 관련 아이템 없음 (orderDao.getOrdersByUserSeq가 null 반환)");
        }
        return orders;
    }
}