package com.a7a7.modeule.payment;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

@Controller
public class PaymentController {

    @Value("${tossPay_api}")
    private String tossPay_api;
    
    @Autowired
    OrderService orderService;

    @GetMapping("/usr/payment/PaymentUsrSuccess")
    public String paymentSuccess(
            @RequestParam(name = "paymentKey") String paymentKey,
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "amount") Long amount,
            Model model) {

        String secretKey = tossPay_api;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentKey", paymentKey);
        payload.put("orderId", orderId);
        payload.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm", request, String.class);

            model.addAttribute("response", response.getBody());
            return "usr/payment/PaymentSuccess";

        } catch (HttpClientErrorException e) {
            return "redirect:/usr/payment/PaymentUsrFail";
        }
    }

    @RequestMapping(value = "/usr/payment/PaymentUsrFail")
    public String paymentFail(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String message,
            Model model) {
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        return "usr/payment/paymentFail";
    }
    
    @PostMapping("/confirm-payment")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> requestData, HttpSession session) {

        String paymentKey = requestData.get("paymentKey");
        String orderId = requestData.get("orderId");
        String amountStr = requestData.get("amount");

        try {
            int amount = Integer.parseInt(amountStr);

            // 1. 토스 결제 승인 요청
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String secretKey = tossPay_api; // 실제 키로 바꾸세요
            String auth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + auth);

            Map<String, String> body = new HashMap<>();
            body.put("orderId", orderId);
            body.put("amount", String.valueOf(amount));

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> tossResponse = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey,
                entity,
                String.class
            );

            // 2. 세션에서 로그인 사용자 ID와 주문 아이템 꺼냄
            String userUiSeq = (String) session.getAttribute("userUiSeq");
            @SuppressWarnings("unchecked")
            List<OrderDto> orderItemList = (List<OrderDto>) session.getAttribute("orderItemList");

            // 3. order 테이블용 OrderDto 생성
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderId(orderId);
            orderDto.setUserUiSeq(userUiSeq);
            orderDto.setTotal_price(amount);
            orderDto.setStatus("결제완료");
            orderDto.setOrderDate(LocalDateTime.now().toString());

            // 4. DB 저장
            orderService.createOrder(orderDto, orderItemList);

            // 5. 세션 정리 (선택)
            session.removeAttribute("orderItemList");

            return ResponseEntity.ok("결제 성공 및 주문 저장 완료");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("에러 발생: " + e.getMessage());
        }
    }

}
