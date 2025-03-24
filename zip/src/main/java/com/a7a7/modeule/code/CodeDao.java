package com.a7a7.modeule.code;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CodeDao {
	
	public int selectOneCount(CodeVo vo);
	
	public List<CodeDto> selectList(CodeVo vo);
	
	public List<CodeDto> selectOne();
	
	public int insert(CodeDto codeDto);
	
    public List<CodeDto> selectListCachedCodeArrayList();
	
}
