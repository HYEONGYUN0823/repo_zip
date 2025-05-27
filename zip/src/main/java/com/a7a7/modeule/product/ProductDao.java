package com.a7a7.modeule.product;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.a7a7.modeule.codegroup.CodeGroupDto;

@Repository
public interface ProductDao {
	
	public List<CodeGroupDto> selectList(ProductVo vo);
	public List<CodeGroupDto> selectProductList(ProductListVo vo);
	
	public ProductDto selectOne(ProductDto productDtoo);
	
	public int insert(ProductDto productDto);
	
	public int update(ProductDto productDto);
	
	public int uelete(ProductDto productDto);
	
	public int selectOneCount(ProductVo vo);
	
	public int updateStockDecrease(Map<String, Object> params);
	
//	public List<ProductDto> filterProducts(int minPrice, int maxPrice, List<Integer> ratings);
	
}
