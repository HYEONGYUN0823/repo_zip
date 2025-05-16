package com.a7a7.modeule.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.modeule.member.MemberDto;
import com.a7a7.modeule.member.MemberService;

import jakarta.servlet.http.HttpSession;


@Controller
public class CheckoutController {
    @Autowired
    MemberService memberService;  // 회원 서비스 주입 (배송지 정보 조회용)

    @RequestMapping(value = "/usr/checkout/CheckoutUsrList")
    public String CheckoutUsrList(HttpSession httpSession, Model model) throws Exception {

        // 1. 세션에서 로그인된 사용자 seq 또는 id 가져오기
        String sessSeqUsr = (String) httpSession.getAttribute("sessSeqUsr");
        if (sessSeqUsr != null) {
            // 2. 사용자 정보 조회
            MemberDto memberDto = new MemberDto();
            memberDto.setSeq(sessSeqUsr);
            MemberDto user = memberService.selectOne(memberDto);

            // 3. Model에 사용자 정보 담기 (배송지 포함)
            model.addAttribute("user", user);
        }
        
		return "usr/checkout/Checkout";
    }
	
}
