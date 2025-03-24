package com.a7a7.modeule.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MemberController {

	@RequestMapping(value = "/xdm/member/MemberXdmList")
	public String MemberXdmList() {
		
		return "xdm/member/MemberXdmList";
	}
	
}
