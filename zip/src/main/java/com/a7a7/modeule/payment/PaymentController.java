package com.a7a7.modeule.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.time.LocalDateTime; // orderDate 직접 설정 시 필요

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.a7a7.modeule.order.OrderDto;
import com.a7a7.modeule.order.OrderService;

import jakarta.servlet.http.HttpSession;
// JSON 파싱을 위해 (선택적, 응답 본문 확인할 때)
// import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class PaymentController {

    @Value("${tossPay_api}")
    private String tossPay_api_secret_key; // 변수명 명확히 (시크릿 키임을 나타냄)

    @Autowired
    OrderService orderService;

    // @Autowired // ObjectMapper 필요시 주입
    // private ObjectMapper objectMapper;

    @GetMapping("/usr/payment/PaymentUsrSuccess")
    public String paymentSuccessPage(
            @RequestParam(name = "paymentKey") String paymentKey, // "paymentKey"라는 이름의 HTTP 파라미터를 찾음
            @RequestParam(name = "orderId") String orderId,       // "orderId"라는 이름의 HTTP 파라미터를 찾음
            @RequestParam(name = "amount") Long amount,          // "amount"라는 이름의 HTTP 파라미터를 찾음
            Model model) {

        System.out.println("### PaymentController.paymentSuccessPage - 요청 받음");
        System.out.println("paymentKey: " + paymentKey + ", orderId: " + orderId + ", amount: " + amount);

        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        // model.addAttribute("isPaymentSuccess", true); // JavaScript에서 API 호출 판단용 플래그 (선택)

        return "usr/payment/PaymentSuccess"; // 결제 성공 후 보여줄 HTML 뷰
    }

    @RequestMapping(value = "/usr/payment/PaymentUsrFail")
    public String paymentFailPage(
            @RequestParam(name = "code", required = false) String code, // name 속성 추가
            @RequestParam(name = "message", required = false) String message, // name 속성 추가
            @RequestParam(name = "orderId", required = false) String orderId, // name 속성 추가
            Model model) {

        System.out.println("### PaymentUsrFail 페이지 요청 받음");
        System.out.println("code: " + code + ", message: " + message + ", orderId: " + orderId);

        model.addAttribute("code", code);
        model.addAttribute("message", message);
        model.addAttribute("orderId", orderId); // 실패한 주문 ID도 전달
        return "usr/payment/paymentFail"; // 실제 결제 실패 후 보여줄 HTML 뷰 이름
    }

    @PostMapping("/confirm-payment")
    @ResponseBody
    public ResponseEntity<?> confirmPaymentAndCreateOrder(@RequestBody Map<String, String> requestData, HttpSession session) { // 메소드명 변경
        System.out.println("### /confirm-payment API 요청 받음: " + requestData);

        String paymentKey = requestData.get("paymentKey");
        String clientOrderId = requestData.get("orderId"); // 클라이언트에서 생성한 주문 ID
        String amountStr = requestData.get("amount");

        if (paymentKey == null || clientOrderId == null || amountStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "필수 결제 정보(paymentKey, orderId, amount)가 누락되었습니다."));
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "금액 정보가 올바르지 않습니다."));
        }

        // 1. 토스페이먼츠 결제 승인 요청 (최종 승인 API 사용)
        HttpHeaders headers = new HttpHeaders();
        String encodedSecretKey = Base64.getEncoder().encodeToString((tossPay_api_secret_key + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> confirmPayload = new HashMap<>();
        confirmPayload.put("paymentKey", paymentKey);
        confirmPayload.put("orderId", clientOrderId);
        confirmPayload.put("amount", amount);

        HttpEntity<Map<String, Object>> confirmRequestEntity = new HttpEntity<>(confirmPayload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> tossConfirmResponse;

        try {
            System.out.println("### 토스페이먼츠 /confirm API 호출 시도...");
            tossConfirmResponse = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm", // 최종 결제 승인 API
                confirmRequestEntity,
                String.class
            );

            System.out.println("### 토스페이먼츠 /confirm API 응답 상태: " + tossConfirmResponse.getStatusCode());
            System.out.println("### 토스페이먼츠 /confirm API 응답 본문: " + tossConfirmResponse.getBody());

            if (tossConfirmResponse.getStatusCode() != HttpStatus.OK) {
                // 응답 본문을 파싱하여 구체적인 에러 메시지 포함 가능
                return ResponseEntity.status(tossConfirmResponse.getStatusCode()).body(Map.of("error", "토스페이먼츠 결제 최종 승인 실패", "details", tossConfirmResponse.getBody()));
            }
            // 성공 시, 응답 본문(JSON)을 파싱하여 결제 상태(예: "status": "DONE") 등을 확인할 수 있습니다.
            // ObjectMapper objectMapper = new ObjectMapper();
            // Map<String, Object> tossResult = objectMapper.readValue(tossConfirmResponse.getBody(), Map.class);
            // if (!"DONE".equals(tossResult.get("status"))) { ... 에러 처리 ... }

        } catch (HttpClientErrorException e) {
            System.err.println("### 토스페이먼츠 /confirm API 호출 중 HttpClientErrorException: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", "토스페이먼츠 통신 오류", "details", e.getResponseBodyAsString()));
        } catch (Exception e) {
            System.err.println("### 토스페이먼츠 /confirm API 호출 중 일반 Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "결제 승인 중 알 수 없는 서버 오류 발생"));
        }

        // 2. 세션에서 로그인 사용자 ID와 주문 아이템 꺼냄
        String userUiSeq = (String) session.getAttribute("sessSeqUsr"); // 실제 세션 키 이름 사용
        @SuppressWarnings("unchecked")
        List<OrderDto> orderItemListFromSession = (List<OrderDto>) session.getAttribute("orderItemList");

        if (userUiSeq == null) {
            System.err.println("### /confirm-payment - userUiSeq가 세션에 없습니다 (로그아웃 상태?).");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 정보가 유효하지 않습니다. 다시 로그인해주세요."));
        }
        if (orderItemListFromSession == null || orderItemListFromSession.isEmpty()) {
            System.err.println("### /confirm-payment - orderItemList가 세션에 없거나 비어있습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "주문할 상품 정보가 없습니다. 장바구니를 확인해주세요."));
        }
        for (OrderDto item : orderItemListFromSession) {
            if (item.getMealKitSeq() == null || item.getMealKitSeq().trim().isEmpty()) {
                System.err.println("### /confirm-payment - orderItemList에 mealKitSeq가 없는 아이템 발견: " + (item.getProductName() != null ? item.getProductName() : "이름 없는 상품"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "주문 상품 정보에 오류가 있습니다 (상품 ID 누락). 상품: " + (item.getProductName() != null ? item.getProductName() : "알 수 없음")));
            }
        }

        // 3. order 테이블용 OrderDto (주문 마스터 정보) 생성
        OrderDto orderMasterDto = new OrderDto();
        orderMasterDto.setOrderId(clientOrderId); // TossPayments의 주문 ID
        orderMasterDto.setUserUiSeq(userUiSeq);
        orderMasterDto.setTotal_price(amount);
        orderMasterDto.setStatus("결제완료"); // 또는 "주문확인중" 등 초기 상태
        // orderMasterDto.setOrderDate(LocalDateTime.now().toString()); // DB에서 NOW() 사용 시 생략

        // 4. DB 저장
        try {
            System.out.println("### orderService.createOrder 호출 시도. orderMasterDto: " + orderMasterDto + ", itemCount: " + orderItemListFromSession.size());
            orderService.createOrder(orderMasterDto, orderItemListFromSession);
            System.out.println("### orderService.createOrder 호출 성공. 생성된 DB 주문 PK (orderMasterDto.seq): " + orderMasterDto.getSeq());
        } catch (Exception e) {
            System.err.println("### orderService.createOrder 호출 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 결제는 성공했지만 주문 저장에 실패한 경우, 해당 결제를 수동으로 취소하는 로직을 고려해야 할 수 있습니다.
            // (또는 관리자 알림 후 수동 처리)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "주문 정보를 데이터베이스에 저장하는 중 오류가 발생했습니다. 관리자에게 문의하세요. 결제 ID: " + paymentKey));
        }

        // 5. 세션 정리
        session.removeAttribute("orderItemList");
        // session.removeAttribute("totalPriceForPayment"); // 등 관련된 다른 세션 정보도 정리

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "결제 성공 및 주문이 정상적으로 처리되었습니다.");
        successResponse.put("orderSeq", orderMasterDto.getSeq()); // DB에 저장된 주문의 PK
        successResponse.put("tossOrderId", clientOrderId);
        return ResponseEntity.ok(successResponse);
    }
}