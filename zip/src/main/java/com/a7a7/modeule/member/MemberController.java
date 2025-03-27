package com.a7a7.modeule.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.modeule.code.CodeDto;
import com.a7a7.modeule.code.CodeService;
import com.a7a7.modeule.code.CodeVo;

import jakarta.servlet.http.HttpSession;


@Controller
public class MemberController {
	@Autowired
	MemberService memberService;
	@Autowired
	CodeService codeService;
	
	@ResponseBody
	@RequestMapping(value = "/xdm/signin/signinXdmProc")
	public Map<String, Object> signinXdmProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		MemberDto reMember = memberService.selectOneLogin(memberDto);
		
		if(reMember != null && reMember.getEmail() != null && !reMember.getEmail().equals(""))	{
			returnMap.put("rt", "success");
			
			httpSession.setAttribute("sessSeqXdm", reMember.getSeq());
			httpSession.setAttribute("sessIdXdm", reMember.getEmail());
			httpSession.setAttribute("sessNameXdm", reMember.getLastName());
		} else {
			returnMap.put("rt", "fail");
		}
	    	
		
	    
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/xdm/signin/signoutXdmProc")
	public Map<String, Object> signoutXdmProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		returnMap.put("rt", "success");
		return returnMap;
	}
	
	@RequestMapping(value = "/xdm/signin/signinXdm")
	public String memberXdmList(HttpSession httpSession, MemberDto memberDto) {
		
		return "xdm/signin/signinXdm";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmList")
	public String memberXdmList(@ModelAttribute("vo") MemberVo vo, Model model, HttpSession httpSession) throws Exception {
		
	    vo.setParamsPaging(memberService.selectOneCount(vo));

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", memberService.selectList(vo));
	    }

		return "xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmForm")
	public String memberXdmForm(@ModelAttribute("vo") MemberVo vo, MemberDto memberDto, Model model) throws Exception{
		System.out.println(memberService.selectOne(memberDto));
		
//		model.addAttribute("codeList", CodeService.selectListCachedCode("1"));
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", memberService.selectOne(memberDto));
//			model.addAttribute("list", codeService.selectList(cvo));
		}
		
		return "xdm/member/MemberXdmForm";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmInst")
	public String memberXdmInst(MemberDto memberDto) {
		memberService.insert(memberDto);
		
		return "redirect:/xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmUpdt")
	public String memberXdmUpdt(MemberDto memberDto) {
		memberService.update(memberDto);
		
		return "redirect:/xdm/member/MemberXdmList";
	}
	
	
	
	

}
