package com.a7a7.modeule.upload;

import org.springframework.stereotype.Repository;

@Repository
public interface UploadDao {
	
	public int insert(UploadDto dto);
	
	public int insertUploaded(UploadDto dto);
	
	public UploadDto selectOne(UploadDto dto);
	
}
