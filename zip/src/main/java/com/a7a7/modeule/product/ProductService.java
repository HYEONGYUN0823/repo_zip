package com.a7a7.modeule.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
    @Transactional // 재고 변경은 중요한 작업이므로 트랜잭션 관리
    public int decreaseStock(String productSeq, int quantityToDecrease) throws Exception {
        System.out.println("### ProductService.decreaseStock - 상품 ID: " + productSeq + ", 감소 수량: " + quantityToDecrease);

        if (productSeq == null || productSeq.trim().isEmpty() || quantityToDecrease <= 0) {
            throw new IllegalArgumentException("상품 ID 또는 수량이 유효하지 않습니다.");
        }

        // 선택적: 현재 재고 확인 로직 (DAO에 SELECT 쿼리가 있다면)
        ProductDto productParam = new ProductDto();
        productParam.setSeq(productSeq);
        ProductDto currentProduct = dao.selectOne(productParam); // 현재 상품 정보 조회

        if (currentProduct == null) {
            System.err.println("### ProductService.decreaseStock - 존재하지 않는 상품 ID: " + productSeq);
            throw new RuntimeException("존재하지 않는 상품입니다. (ID: " + productSeq + ")");
        }
        if (currentProduct.getStock() == null || currentProduct.getStock() < quantityToDecrease) {
            System.err.println("### ProductService.decreaseStock - 재고 부족. 상품 ID: " + productSeq + ", 현재 재고: " + currentProduct.getStock() + ", 요청 수량: " + quantityToDecrease);
            throw new RuntimeException("재고가 부족합니다. (상품: " + currentProduct.getMealKitName() + ")");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("productSeq", productSeq);
        params.put("quantityToDecrease", quantityToDecrease);
        int updatedRows = dao.updateStockDecrease(params);

        if (updatedRows == 0) {
            // 이 경우는 DAO의 UPDATE 쿼리에 AND stock >= quantity 조건이 있고, 그 조건에 걸렸거나,
            // productSeq가 DB에 없는 경우 등 (위의 selectOne으로 이미 확인했으므로 후자는 드묾)
            System.err.println("### ProductService.decreaseStock - 재고 업데이트 실패 (0 rows affected). 상품 ID: " + productSeq + ". 동시성 문제 또는 재고 부족 조건 실패 가능성.");
            // 여기서 다시 한번 재고를 확인하고 예외를 발생시킬 수 있음
            ProductDto refreshedProduct = dao.selectOne(productParam);
            if (refreshedProduct.getStock() < quantityToDecrease) {
                 throw new RuntimeException("재고가 부족하여 업데이트되지 않았습니다. (상품: " + refreshedProduct.getMealKitName() + ")");
            }
            // 다른 이유로 업데이트가 안 된 경우, 심각한 오류일 수 있음
            throw new RuntimeException("알 수 없는 이유로 재고 업데이트에 실패했습니다. 상품 ID: " + productSeq);
        }
        System.out.println("### ProductService.decreaseStock - 재고 감소 성공. 상품 ID: " + productSeq + ", 업데이트된 행 수: " + updatedRows);
        return updatedRows;
    }

//	public List<ProductDto> filterProducts(int minPrice, int maxPrice, List<Integer> ratings) {
//	    return dao.filterProducts(minPrice, maxPrice, ratings);
//	}
	
	
	
}
