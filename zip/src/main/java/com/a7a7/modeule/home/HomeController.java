package com.a7a7.modeule.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	
	@RequestMapping(value = "/xdm/home/HomeXdmList")
	public String homeXdmList() {
		
		return "xdm/home/HomeXdmList";
	}
	
	
}
