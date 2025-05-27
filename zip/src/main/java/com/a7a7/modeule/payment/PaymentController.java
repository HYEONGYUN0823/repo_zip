package com.a7a7.modeule.payment; // ★★★ 실제 PaymentController 패키지 경로 ★★★

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // Idempotency-Key 생성용

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
// import com.fasterxml.jackson.databind.ObjectMapper; // 필요시

@Controller
public class PaymentController {

    @Value("${tossPay_api}") // application.properties 또는 yml에 정의된 값
    private String tossPayApiSecretKey; // 변수명 일관성 및 명확성

    @Autowired
    private OrderService orderService;

    // @Autowired
    // private ObjectMapper objectMapper; // Toss 응답 본문 상세 파싱용

    @GetMapping("/usr/payment/PaymentUsrSuccess")
    public String paymentSuccessPage(
            @RequestParam(name = "paymentKey") String paymentKey,
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "amount") Long amount,
            Model model) {

        System.out.println("### PaymentController.paymentSuccessPage - 요청 받음. paymentKey: " + paymentKey + ", orderId: " + orderId + ", amount: " + amount);

        // 이 값들을 모델에 담아 PaymentSuccess.html로 전달하여 JavaScript에서 사용
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);

        return "usr/payment/PaymentSuccess"; // resources/templates/usr/payment/PaymentSuccess.html
    }

    @RequestMapping(value = "/usr/payment/PaymentUsrFail")
    public String paymentFailPage(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "message", required = false) String message,
            @RequestParam(name = "orderId", required = false) String orderId,
            Model model) {

        System.out.println("### PaymentController.paymentFailPage - 요청 받음. code: " + code + ", message: " + message + ", orderId: " + orderId);

        model.addAttribute("code", code);
        model.addAttribute("message", message);
        model.addAttribute("orderId", orderId);
        return "usr/payment/paymentFail"; // resources/templates/usr/payment/paymentFail.html
    }

    @PostMapping("/confirm-payment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmPaymentAndCreateOrder(@RequestBody Map<String, String> requestData, HttpSession session) {
        System.out.println("### PaymentController./confirm-payment - API 요청 받음: " + requestData);

        String paymentKey = requestData.get("paymentKey");
        String clientSideOrderId = requestData.get("orderId"); // 클라이언트에서 생성/전달한 주문 ID
        String amountStr = requestData.get("amount");

        // 필수 파라미터 검증
        if (paymentKey == null || clientSideOrderId == null || amountStr == null) {
            System.err.println("### PaymentController./confirm-payment - 필수 파라미터 누락.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "필수 결제 정보(paymentKey, orderId, amount)가 누락되었습니다."));
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) throw new NumberFormatException("Amount must be positive");
        } catch (NumberFormatException e) {
            System.err.println("### PaymentController./confirm-payment - 금액 정보 오류: " + amountStr);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "금액 정보가 올바르지 않습니다."));
        }

        // 1. 토스페이먼츠 결제 최종 승인 요청
        HttpHeaders headers = new HttpHeaders();
        String encodedSecretKey = Base64.getEncoder().encodeToString((tossPayApiSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 멱등키 추가 (Toss Payments 권장)
        String idempotencyKey = UUID.randomUUID().toString();
        headers.set("Idempotency-Key", idempotencyKey);


        Map<String, Object> confirmPayload = new HashMap<>();
        confirmPayload.put("paymentKey", paymentKey);
        confirmPayload.put("orderId", clientSideOrderId);
        confirmPayload.put("amount", amount);

        HttpEntity<Map<String, Object>> confirmRequestEntity = new HttpEntity<>(confirmPayload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> tossConfirmResponse;

        try {
            System.out.println("### PaymentController - 토스페이먼츠 /v1/payments/confirm API 호출 시도. Payload: " + confirmPayload);
            tossConfirmResponse = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm", // 최종 결제 승인 API
                confirmRequestEntity,
                String.class
            );

            System.out.println("### PaymentController - 토스페이먼츠 /confirm API 응답 상태: " + tossConfirmResponse.getStatusCode());
            System.out.println("### PaymentController - 토스페이먼츠 /confirm API 응답 본문: " + tossConfirmResponse.getBody());

            if (tossConfirmResponse.getStatusCode() != HttpStatus.OK) {
                // 일반적으로 HttpClientErrorException이 발생하지만, 방어적으로 체크
                System.err.println("### PaymentController - 토스페이먼츠 결제 최종 승인 실패 (상태 코드 NOT OK).");
                return ResponseEntity.status(tossConfirmResponse.getStatusCode()).body(Map.of("error", "토스페이먼츠 결제 최종 승인에 실패했습니다.", "details", tossConfirmResponse.getBody()));
            }
            // 성공 시, 응답 본문(JSON)을 파싱하여 실제 결제 상태 (예: "status": "DONE") 등을 확인하는 것이 좋음
            // 예: Map<String, Object> tossResult = objectMapper.readValue(tossConfirmResponse.getBody(), Map.class);
            // if (!"DONE".equals(tossResult.get("status"))) { /* 에러 처리, 결제 취소 */ }

        } catch (HttpClientErrorException e) {
            System.err.println("### PaymentController - 토스페이먼츠 /confirm API 호출 중 HttpClientErrorException: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", "토스페이먼츠와 통신 중 오류가 발생했습니다.", "details", e.getResponseBodyAsString()));
        } catch (Exception e) {
            System.err.println("### PaymentController - 토스페이먼츠 /confirm API 호출 중 일반 Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "결제 승인 처리 중 알 수 없는 서버 오류가 발생했습니다."));
        }

        // --- 결제 승인 성공 후 주문 생성 및 재고 처리 ---

        String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        @SuppressWarnings("unchecked")
        List<OrderDto> orderItemListFromSession = (List<OrderDto>) session.getAttribute("orderItemList");

        // 세션 정보 유효성 검사
        if (userUiSeq == null) {
            System.err.println("### PaymentController - userUiSeq가 세션에 없음. 결제는 승인되었으나 주문 생성 불가. PaymentKey: " + paymentKey);
            cancelTossPayment(paymentKey, "세션 만료 또는 사용자 정보 없음"); // ★★★ 결제 취소 호출 ★★★
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 정보가 만료되었습니다. 다시 시도해주세요."));
        }
        if (orderItemListFromSession == null || orderItemListFromSession.isEmpty()) {
            System.err.println("### PaymentController - orderItemList가 세션에 없음. 결제는 승인되었으나 주문 생성 불가. PaymentKey: " + paymentKey);
            cancelTossPayment(paymentKey, "주문 상품 정보 없음"); // ★★★ 결제 취소 호출 ★★★
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "주문할 상품 정보가 없습니다. 장바구니를 다시 확인해주세요."));
        }


        OrderDto orderMasterDto = new OrderDto();
        orderMasterDto.setOrderId(clientSideOrderId); // TossPayments의 주문 ID
        orderMasterDto.setUserUiSeq(userUiSeq);
        orderMasterDto.setTotal_price(amount);
        orderMasterDto.setStatus("결제완료");
        // orderMasterDto.setOrderDate(LocalDateTime.now().toString()); // DB에서 NOW() 사용 시 불필요

        try {
            System.out.println("### PaymentController - orderService.createOrder 호출 시도. orderMasterDto: " + orderMasterDto.getOrderId() + ", itemCount: " + orderItemListFromSession.size());
            orderService.createOrder(orderMasterDto, orderItemListFromSession); // 이 메소드가 Exception을 던질 수 있음
            System.out.println("### PaymentController - orderService.createOrder 호출 성공. 생성된 DB 주문 PK: " + orderMasterDto.getSeq());

            session.removeAttribute("orderItemList"); // 성공 시 세션 정리

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "결제 성공 및 주문이 정상적으로 처리되었습니다.");
            successResponse.put("orderSeq", orderMasterDto.getSeq()); // DB에 저장된 주문의 PK
            successResponse.put("tossOrderId", clientSideOrderId);
            return ResponseEntity.ok(successResponse);

        } catch (IllegalArgumentException | IllegalStateException iae) { // OrderService 또는 ProductService에서 발생한 유효성/상태 관련 예외
            System.err.println("### PaymentController - 주문 생성 중 유효성/상태 오류 (OrderService): " + iae.getMessage());
            cancelTossPayment(paymentKey, "주문 처리 오류: " + iae.getMessage()); // ★★★ 결제 취소 호출 ★★★
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "주문 처리 실패: " + iae.getMessage()));
        } catch (RuntimeException re) { // OrderService 또는 ProductService에서 재고 부족 또는 DB 오류 등으로 발생시킨 일반 런타임 예외
            System.err.println("### PaymentController - 주문 생성 중 런타임 오류 (OrderService): " + re.getMessage());
            cancelTossPayment(paymentKey, "주문 처리 시스템 오류: " + re.getMessage()); // ★★★ 결제 취소 호출 ★★★
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "주문 처리 중 시스템 오류가 발생했습니다. 관리자에게 문의하세요. (결제ID: " + paymentKey + ")"));
        } catch (Exception e) { // 그 외 OrderService에서 던진 모든 예외 (메소드 시그니처에 throws Exception이 있으므로)
            System.err.println("### PaymentController - 주문 생성 중 OrderService 예외: " + e.getMessage());
            e.printStackTrace();
            cancelTossPayment(paymentKey, "알 수 없는 주문 처리 오류"); // ★★★ 결제 취소 호출 ★★★
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "주문 처리 중 예기치 않은 오류가 발생했습니다. 관리자에게 문의하세요. (결제ID: " + paymentKey + ")"));
        }
    }

    /**
     * Toss Payments 결제 취소 API를 호출하는 메소드.
     * 주문 처리 중 DB 저장 실패 등으로 인해 이미 승인된 결제를 취소해야 할 때 사용.
     * @param paymentKey 취소할 결제의 paymentKey
     * @param cancelReason 취소 사유
     */
    private void cancelTossPayment(String paymentKey, String cancelReason) {
        System.out.println("### PaymentController.cancelTossPayment - 결제 취소 시도. PaymentKey: " + paymentKey + ", 사유: " + cancelReason);
        if (paymentKey == null || paymentKey.trim().isEmpty()) {
            System.err.println("### PaymentController.cancelTossPayment - paymentKey가 없어 결제 취소를 진행할 수 없습니다.");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            String encodedSecretKey = Base64.getEncoder().encodeToString((tossPayApiSecretKey + ":").getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + encodedSecretKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String idempotencyKey = UUID.randomUUID().toString(); // 각 취소 요청마다 고유한 키 사용
            headers.set("Idempotency-Key", idempotencyKey);

            Map<String, String> payload = new HashMap<>();
            payload.put("cancelReason", cancelReason != null ? cancelReason : "주문 처리 오류로 인한 자동 취소");
            // 필요시 추가 파라미터: cancelAmount, refundReceiveAccount 등 (Toss 문서 참조)

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();

            System.out.println("### PaymentController.cancelTossPayment - Toss 결제 취소 API 호출. URL: " + "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel" + ", Payload: " + payload);
            ResponseEntity<String> cancelResponse = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel",
                requestEntity,
                String.class
            );

            if (cancelResponse.getStatusCode() == HttpStatus.OK) {
                System.out.println("### PaymentController.cancelTossPayment - 결제 취소 성공. 응답: " + cancelResponse.getBody());
                // TODO: 결제 취소 성공 후 내부 시스템에 상태 업데이트 또는 관리자 알림
            } else {
                System.err.println("### PaymentController.cancelTossPayment - 결제 취소 API 응답 실패. 상태: " + cancelResponse.getStatusCode() + ", 응답: " + cancelResponse.getBody());
                // TODO: 결제 취소 API 호출은 성공했으나, 응답이 OK가 아닌 경우의 처리 (관리자 알림 등)
            }
        } catch (HttpClientErrorException hcee) {
            System.err.println("### PaymentController.cancelTossPayment - 결제 취소 중 HttpClientErrorException: " + hcee.getStatusCode() + " - " + hcee.getResponseBodyAsString());
            hcee.printStackTrace();
            // TODO: 결제 취소 중 API 통신 오류 발생 시 처리 (관리자에게 심각한 오류 알림)
        } catch (Exception e) {
            System.err.println("### PaymentController.cancelTossPayment - 결제 취소 중 일반 Exception: " + e.getMessage());
            e.printStackTrace();
            // TODO: 결제 취소 중 예외 발생 시 처리 (관리자에게 심각한 오류 알림)
        }
    }
}