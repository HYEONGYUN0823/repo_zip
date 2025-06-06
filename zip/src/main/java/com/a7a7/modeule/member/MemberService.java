package com.a7a7.modeule.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
	@Autowired
	MemberDao memberDao;

	public int selectOneCount(MemberVo vo) {
		return memberDao.selectOneCount(vo);
	}
	
	public List<MemberDto> selectList(MemberVo vo) {
		return memberDao.selectList(vo);
	}
	
	public MemberDto selectOne(MemberDto memberDto) {
		return memberDao.selectOne(memberDto);
	}
	
	public int insert(MemberDto memberDto) {
		return memberDao.insert(memberDto);
	}
	
	public int update(MemberDto memberDto) {
		return memberDao.update(memberDto);
	}
	
	public int uelete(MemberDto memberDto) {
		return memberDao.uelete(memberDto);
	}
	
	public MemberDto selectOneLogin(MemberDto memberDto) {
		return memberDao.selectOneLogin(memberDto);
	}
	
	public int checkId(MemberDto memberDto) {
		return memberDao.checkId(memberDto);
	}
	
	public MemberDto checkPassword(MemberDto memberDto) {
		return memberDao.checkPassword(memberDto);
	}
	
	public int userUpdate(MemberDto memberDto) {
		return memberDao.userUpdate(memberDto);
	}
	
	public int passwordChange(MemberDto memberDto) {
		return memberDao.passwordChange(memberDto);
	}
	
}
