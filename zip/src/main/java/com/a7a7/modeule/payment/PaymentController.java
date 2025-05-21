package com.a7a7.modeule.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PaymentController {
	
	@RequestMapping(value = "/usr/payment/PaymentUsrSuccess")
	public String paymentSuccess() {
		
		return "usr/payment/PaymentSuccess";
	}
	
	@RequestMapping(value = "/usr/payment/PaymentUsrFail")
	public String paymentFail() {
		
		return "usr/payment/paymentFail";
	}
	
	
}
