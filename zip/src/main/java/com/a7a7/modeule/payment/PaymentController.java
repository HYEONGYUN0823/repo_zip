package com.a7a7.modeule.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class PaymentController {

    @Value("${tossPay_api}")
    private String tossPay_api;

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

}
