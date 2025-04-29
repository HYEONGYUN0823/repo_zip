package com.a7a7.modeule.upload;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a7a7.common.util.UtilDateTime;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class UploadService {
    @Autowired
    private UploadDao dao;
    @Value(value = "${cloud_aws_bucket}")
	private String bucket;


    public void uploadFilesToS3(MultipartFile[] multipartFiles, UploadDto dto, String tableName, int type, int maxNumber, String pSeq, UploadDao dao, AmazonS3Client amazonS3Client) throws Exception {
		
		for(int i=0; i<multipartFiles.length; i++) {
			
			if(!multipartFiles[i].isEmpty()) {
				
//				String className = dto.getClass().getSimpleName().toString().toLowerCase();
				// 접두사: 4, 접미사: uploaded (8) 삭제
				String className = tableName.substring(4).substring(0,tableName.length()-12);		
				String fileName = multipartFiles[i].getOriginalFilename();
				String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
				String uuid = UUID.randomUUID().toString();
				String uuidFileName = uuid + "." + ext;
				String pathModule = className;
				String nowString = UtilDateTime.nowString();
				String pathDate = nowString.substring(0,4) + "/" + nowString.substring(5,7) + "/" + nowString.substring(8,10); 
				String path = pathModule + "/" + type + "/" + pathDate + "/";
//				String pathForView = Constants.UPLOADED_PATH_PREFIX_FOR_VIEW_LOCAL + "/" + pathModule + "/" + type + "/" + pathDate + "/";
				
				
		        ObjectMetadata metadata = new ObjectMetadata();
		        metadata.setContentLength(multipartFiles[i].getSize());
		        metadata.setContentType(multipartFiles[i].getContentType());
		        
		        amazonS3Client.putObject(bucket, path + uuidFileName, multipartFiles[i].getInputStream(), metadata);
//		        https://doris-tt.s3.ap-northeast-2.amazonaws.com/Goods/1002/2025/04/17/de100958-3684-4ed1-94d9-de77c98dbb94.jpeg
		        
		        String objectUrl = amazonS3Client.getUrl(bucket, path + uuidFileName).toString();
		        
				dto.setPath(objectUrl);
				dto.setOriginalName(fileName);
				dto.setUuidName(uuidFileName);
				dto.setExt(ext);
				dto.setSize(multipartFiles[i].getSize());
				
				dto.setTableName(tableName);
				dto.setType(type);
	//			dto.setDefaultNy();
				dto.setSort(maxNumber + i);
				dto.setPseq(pSeq);
				
				dao.insertUploaded(dto);
			}
		}
	}
    
    public UploadDto selectOne(UploadDto dto) {
    	return dao.selectOne(dto);
    }
}