package com.a7a7.modeule.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.modeule.codegroup.CodeGroupVo;


@Controller
public class MemberController {
	@Autowired
	MemberService memberService;
	
	@RequestMapping(value = "/xdm/member/MemberXdmList")
	public String MemberXdmList(@ModelAttribute("vo") MemberVo vo, Model model) throws Exception {
		
	    vo.setParamsPaging(memberService.selectOneCount());

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", memberService.selectList(vo));
	    }

		return "xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmForm")
	public String MemberXdmForm() {
		
		return "xdm/member/MemberXdmForm";
	}
	

}
