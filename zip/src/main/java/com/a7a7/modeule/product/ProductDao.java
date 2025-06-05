package com.a7a7.modeule.product;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
// import com.a7a7.modeule.codegroup.CodeGroupDto; // ProductDto를 사용하므로 주석 처리

@Repository
public interface ProductDao {

    public List<ProductDto> selectList(ProductVo vo); // XDM용, ProductDto 반환으로 변경
    public List<ProductDto> selectProductList(ProductVo vo); // 사용자 초기 목록, ProductDto 반환, ProductVo 파라미터
    public ProductDto selectOne(ProductDto productDto); // 상세 조회
    public int insert(ProductDto productDto);
    public int update(ProductDto productDto);
    public int uelete(ProductDto productDto); // 논리적 삭제 (delNy = 1)
    public int selectOneCount(ProductVo vo); // XDM용 카운트
    public int updateStockDecrease(Map<String, Object> params);

    // --- AJAX 필터링 및 정렬을 위한 메서드 추가 ---
    public List<ProductDto> selectFilteredProductList(ProductVo vo);
    public int selectFilteredProductListCount(ProductVo vo);
}