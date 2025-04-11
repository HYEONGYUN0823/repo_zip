package com.a7a7.modeule.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.code.CodeService;

import jakarta.servlet.http.HttpSession;


@Controller
public class MemberController {
	@Autowired
	MemberService memberService;
	@Autowired
	CodeService codeService;
	
	public String encodeBcrypt(String planeText, int strength) {
		  return new BCryptPasswordEncoder(strength).encode(planeText);
	}

			
	public boolean matchesBcrypt(String planeText, String hashValue, int strength) {
	  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(strength);
	  return passwordEncoder.matches(planeText, hashValue);
	}
	
	private void setSearch(MemberVo vo) {
		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
	}
	
	@ResponseBody
	@RequestMapping(value = "/xdm/signin/signinXdmProc")
	public Map<String, Object> signinXdmProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		MemberDto reMember = memberService.selectOneLogin(memberDto);
		
		if(reMember != null)	{
			returnMap.put("rt", "success");
			
			httpSession.setAttribute("sessSeqXdm", reMember.getSeq());
			httpSession.setAttribute("sessIdXdm", reMember.getiD());
			httpSession.setAttribute("sessPassWordXdm", reMember.getPassWord());
			httpSession.setAttribute("sessNameXdm", reMember.getName());
		} else {
			returnMap.put("rt", "fail");
		}
	    	
		
	    
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/xdm/signin/signoutXdmProc")
	public Map<String, Object> signoutXdmProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		httpSession.setAttribute("sessSeqXdm", null);
		httpSession.setAttribute("sessIdXdm", null);
		httpSession.setAttribute("sessPasswordXdm", null);
		returnMap.put("rt", "success");
		return returnMap;
	}
	
	@RequestMapping(value = "/xdm/signin/signinXdm")
	public String memberXdmList(HttpSession httpSession, MemberDto memberDto) throws Exception {
		
		return "xdm/signin/signinXdm";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmList")
	public String memberXdmList(@ModelAttribute("vo") MemberVo vo, Model model, HttpSession httpSession, MemberDto memberDto) throws Exception {
		
		setSearch(vo);
	    vo.setParamsPaging(memberService.selectOneCount(vo));

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", memberService.selectList(vo));
	        model.addAttribute("memberDto", memberDto); // MemberDto 값도 유지
	    }
	    System.out.println("shAdmSignin: " + vo.getShAdmSignin());
	    
//	    model.addAttribute("vo", vo); // MemberVo 값 유지
	    
		return "xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmForm")
	public String memberXdmForm(@ModelAttribute("vo") MemberVo vo, MemberDto memberDto, Model model) throws Exception {
		
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", memberService.selectOne(memberDto));
		}
		System.out.println("vo.getSeq() = " + vo.getSeq());
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
	
	@RequestMapping(value = "/xdm/member/MemberXdmUele")
	public String memberXdmUele(MemberDto memberDto) {
		memberService.uelete(memberDto);
		
		return "redirect:/xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/usr/userUi/MemberUsrForm")
	public String userUiSignup(@ModelAttribute("vo") MemberVo vo, MemberDto memberDto, Model model) throws Exception {
		if(vo.getSeq() == null) {
		    vo.setSeq("0");
		}
		
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", memberService.selectOne(memberDto));
		}
		System.out.println("vo.getSeq() = " + vo.getSeq());
		return "usr/userUi/MemberUsrForm";
	}
	
	@RequestMapping(value = "/usr/userUi/MemberUsrInst")
	public String memberUsrInst(MemberDto memberDto) {
		memberDto.setPassWord(encodeBcrypt(memberDto.getPassWord(), 10));
		
		memberService.insert(memberDto);
		
		return "redirect:/usr/signin/signinUsr";
	}
	
	@ResponseBody
	@RequestMapping("/usr/userUi/checkId")
	public Map<String, Object> checkId(MemberDto memberDto) {
		int count = memberService.checkId(memberDto); // memberDto에 iD가 담겨 있음

		Map<String, Object> result = new HashMap<>();
		result.put("exists", count > 0); // 이미 존재하면 true, 아니면 false

		return result;
	}
	
	@RequestMapping(value = "/usr/signin/signinUsr")
	public String memberUsrList(HttpSession httpSession, MemberDto memberDto) throws Exception {
		
		return "usr/signin/SigninUsr";
	}
	
	@ResponseBody
	@RequestMapping(value = "/usr/signin/signinUsrProc")
	public Map<String, Object> signinUsrProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		MemberDto reMember = memberService.selectOneLogin(memberDto);
		
		boolean check = matchesBcrypt(memberDto.getPassWord(), reMember.getPassWord(), 10);
		
		if(reMember != null && check)	{
			returnMap.put("rt", "success");
			
			httpSession.setAttribute("sessSeqUsr", reMember.getSeq());
			httpSession.setAttribute("sessIdUsr", reMember.getiD());
			httpSession.setAttribute("sessPassWordUsr", reMember.getPassWord());
			httpSession.setAttribute("sessNameUsr", reMember.getName());
		} else {
			returnMap.put("rt", "fail");
		}
		
		return returnMap;
	}
	
}
