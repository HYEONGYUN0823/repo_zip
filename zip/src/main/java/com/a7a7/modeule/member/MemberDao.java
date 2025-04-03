package com.a7a7.modeule.member;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a7a7.modeule.code.CodeDto;

@Repository
public interface MemberDao {

	public List<MemberDto> selectList(MemberVo vo);
	
	public MemberDto selectOne(MemberDto memberDto);
	
	public int insert(MemberDto memberDto);
	
	public int update(MemberDto memberDto);
	
	public int uelete(MemberDto memberDto);
	
	public int selectOneCount(MemberVo vo);
	
	public MemberDto selectOneLogin(MemberDto memberDto);
	
}
