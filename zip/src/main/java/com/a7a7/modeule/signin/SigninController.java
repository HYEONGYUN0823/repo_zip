package com.a7a7.modeule.signin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.modeule.member.MemberDto;

import jakarta.servlet.http.HttpSession;

@Controller
public class SigninController {
	@Autowired
	SigninService service;
	
//	@ResponseBody
//	@RequestMapping(value = "/xdm/signin/signinXdmProc")
//	public Map<String, Object> signinXdmProc(MemberDto dto, HttpSession httpSession) throws Exception {
//		Map<String, Object> returnMap = new HashMap<String, Object>();
//		
//	}
}
