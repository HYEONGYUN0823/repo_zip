package com.a7a7.modeule.codegroup;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CodeGroupDao {
	
	public List<CodeGroupDto> selectList(CodeGroupVo vo);
	
	public CodeGroupDto selectOne(CodeGroupVo vo);
	
	public List<CodeGroupDto> selectListWithoutPaging();
	
	public int insert(CodeGroupDto codeGroupDto);
	
	public int update(CodeGroupDto codeGroupDto);
	
	public int selectOneCount(CodeGroupVo vo);
	
	public int uelete(CodeGroupDto codeGroupDto);
	
}
