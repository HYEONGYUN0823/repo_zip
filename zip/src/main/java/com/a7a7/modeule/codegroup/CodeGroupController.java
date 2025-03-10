package com.a7a7.modeule.codegroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CodeGroupController {
	@Autowired
	CodeGroupService codeGroupService;
	
	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmList")
	public String CodeGroupXdmList() {
		
		return "xdm/codegroup/CodeGroupXdmList";
	}
	
	
	
}
