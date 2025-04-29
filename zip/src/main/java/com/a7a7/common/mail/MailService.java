package com.a7a7.common.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.a7a7.modeule.member.MemberDto;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	@Autowired
	private JavaMailSender javaMailSender;
	
//	회원가입 축하 메일
    public void sendMailWelcome(MemberDto memberDto) throws Exception{
    	
    	MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    	MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    	mimeMessageHelper.setTo(memberDto.getEmail()); 
    	mimeMessageHelper.setSubject("가입을 축하합니다!");
    	mimeMessageHelper.setText("축하합니다!", true); 
    	javaMailSender.send(mimeMessage);
    	
    }
	
}
