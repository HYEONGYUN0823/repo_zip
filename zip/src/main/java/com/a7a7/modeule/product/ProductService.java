package com.a7a7.modeule.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a7a7.modeule.codegroup.CodeGroupDto;
import com.a7a7.modeule.codegroup.CodeGroupVo;

@Service
public class ProductService {
	@Autowired
	ProductDao productDao;

	public int selectOneCount(ProductVo vo) {
		return productDao.selectOneCount(vo);
	}
	
	public List<CodeGroupDto> selectList(ProductVo vo) {
		return productDao.selectList(vo);
	}
	
	public ProductDto selectOne(ProductVo vo) {
		return productDao.selectOne(vo);
	}
	
	public int insert(ProductDto productDto) {
		return productDao.insert(productDto);
	}
	
	public int update(ProductDto productDto) {
		return productDao.update(productDto);
	}
	
}
