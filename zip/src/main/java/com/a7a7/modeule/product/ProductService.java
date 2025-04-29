package com.a7a7.modeule.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a7a7.modeule.codegroup.CodeGroupDto;
import com.a7a7.modeule.upload.UploadDao;
import com.a7a7.modeule.upload.UploadService;
import com.amazonaws.services.s3.AmazonS3Client;

@Service
public class ProductService extends UploadService {
	@Autowired
	ProductDao dao;
//	for aws.s3 fileupload s
	@Autowired
	private AmazonS3Client amazonS3Client;
	@Autowired
	UploadDao uploadDao;

	public int selectOneCount(ProductVo vo) {
		return dao.selectOneCount(vo);
	}
	
	public List<CodeGroupDto> selectList(ProductVo vo) {
		return dao.selectList(vo);
	}
	public List<CodeGroupDto> selectProductList(ProductListVo vo) {
		return dao.selectProductList(vo);
	}
	
	public ProductDto selectOne(ProductDto productDto) {
		return dao.selectOne(productDto);
	}
	
	public int insert(ProductDto dto) throws Exception {
		dao.insert(dto);
			uploadFilesToS3(
    			dto.getUploadImg1()
    			, dto
    			, "infrBannerUploaded"
    			, dto.getUploadImg1Type()
    			, dto.getUploadImg1MaxNumber()
    			, dto.getSeq()
    			, uploadDao
    			, amazonS3Client);
    	return 1;
    }
	
	public int update(ProductDto productDto) {
		return dao.update(productDto);
	}
	
	public int uelete(ProductDto productDto) {
		return dao.uelete(productDto);
	}
	
	
	
}
