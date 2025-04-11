package com.a7a7.modeule.userUi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class userUiController {
	
	@RequestMapping(value = "/usr/index/Index")
	public String userIndex() {
		
		return "usr/index/Index";
	}
	
	
}
