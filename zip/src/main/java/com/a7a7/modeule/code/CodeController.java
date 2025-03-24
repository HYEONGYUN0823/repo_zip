package com.a7a7.modeule.code;

import java.lang.ProcessBuilder.Redirect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CodeController {
	@Autowired
	CodeService codeService;
	
	@RequestMapping(value = "/xdm/code/CodeXdmList")
	public String codeXdmList(Model model, CodeVo vo) {
		vo.setParamsPaging(codeService.selectOneCount());
		
		model.addAttribute("list", codeService.selectList(vo));
		model.addAttribute("vo", vo);
		
		return "xdm/code/CodeXdmList";
	}
	
//	@RequestMapping(value = "/xdm/code/CodeXdmView")
//	public String requestMethodName(Model model, CodeDto codeDto) {
//		model.addAttribute("item", codeService.selectOne(codeDto));
//		
//		return "xdm/code/CodeXdmView";
//	}
	
	
	@RequestMapping(value = "/xdm/code/CodeXdmForm")
	public String CodeXdmForm(Model model) {
		model.addAttribute("listCodeGroup", codeService.selectOne());
		
		return "xdm/code/CodeXdmForm";
	}
	
	@RequestMapping(value = "xdm/code/CodeXdmInst")
	public String codeXdmInst(CodeDto codeDto) {
		codeService.insert(codeDto);
		
		return "redirect:/xdm/code/CodeXdmList";
	}
	
}
