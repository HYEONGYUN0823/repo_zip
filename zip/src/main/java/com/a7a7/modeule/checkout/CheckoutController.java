package com.a7a7.modeule.checkout;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class CheckoutController {
	
	@RequestMapping(value = "/usr/checkout/CheckoutUsrList")
	public String CheckoutUsrList() {
		
		return "usr/checkout/Checkout";
	}
	
	
}
