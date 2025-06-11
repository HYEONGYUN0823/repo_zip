package com.a7a7.modeule.product;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.a7a7.modeule.codegroup.CodeGroupService;
import com.a7a7.modeule.upload.UploadDao;
import com.a7a7.modeule.upload.UploadDto;
import com.amazonaws.services.s3.AmazonS3Client;

@Service
public class ProductService extends com.a7a7.modeule.upload.UploadService { // FQCN 사용 유지
    @Autowired
    ProductDao dao;
    @Autowired
    CodeGroupService codeGroupService;

    @Autowired
    UploadDao uploadDao; // UploadService를 상속하므로, 해당 클래스에 정의되어 있다면 중복 주입일 수 있음
    @Autowired
    AmazonS3Client amazonS3Client; // UploadService를 상속하므로, 해당 클래스에 정의되어 있다면 중복 주입일 수 있음

    public int selectOneCount(ProductVo vo) {
        return dao.selectOneCount(vo);
    }

    public List<ProductDto> selectList(ProductVo vo) {
        List<ProductDto> list = dao.selectList(vo);
        for (ProductDto dto : list) {
            setBrandNameStringFromCode(dto);
        }
        return list;
    }

    public List<ProductDto> selectProductList(ProductVo vo) {
        List<ProductDto> list = dao.selectProductList(vo);
        for (ProductDto dto : list) {
            setBrandNameStringFromCode(dto);
        }
        return list;
    }

    public ProductDto selectOne(ProductDto productDto) {
        ProductDto item = dao.selectOne(productDto);
        if (item != null) {
            setBrandNameStringFromCode(item);
        }
        return item;
    }

    @Transactional
    public int insert(ProductDto dto) throws Exception {
        int result = dao.insert(dto); // insert 후 dto.seq에 last_insert_id가 채워짐
        MultipartFile[] files = dto.getUploadImg1();
        if (files != null && files.length > 0) {
            boolean hasFilesToUpload = false;
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    hasFilesToUpload = true;
                    break;
                }
            }
            if (hasFilesToUpload) {
                 uploadFilesToS3(files, dto, "productUploaded", dto.getUploadImg1Type(),
                                 dto.getUploadImg1MaxNumber(), dto.getSeq(), uploadDao, amazonS3Client);
            }
        }
        return result; // 보통 insert 성공 시 1을 반환하거나, 생성된 seq를 반환할 수도 있음
    }

    @Transactional
    public int update(ProductDto productDto) {
        // TODO: 파일 관련 로직 (업데이트 시 기존 파일 처리 및 새 파일 업로드)
        return dao.update(productDto);
    }

    @Transactional
    public int uelete(ProductDto productDto) { // 논리적 삭제
        // TODO: 관련 파일도 논리적으로 삭제 처리하거나 S3에서 실제 삭제할지 정책 결정
        return dao.uelete(productDto);
    }

    // --- OrderService에서 호출할 재고 감소 메서드 ---
    /**
     * 지정된 상품의 재고를 감소시킵니다.
     * 이 메서드는 재고 확인 및 실제 DB 업데이트를 포함합니다.
     * @param productSeq 상품 ID (mealKit seq)
     * @param quantityToDecrease 감소시킬 수량
     * @return 업데이트된 행의 수 (보통 1)
     * @throws Exception 재고 부족 또는 DB 오류 발생 시
     */
    @Transactional
    public int decreaseStock(String productSeq, int quantityToDecrease) throws Exception {
        System.out.println("### ProductService.decreaseStock (public) - 상품 ID: " + productSeq + ", 감소 수량: " + quantityToDecrease);

        if (productSeq == null || productSeq.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 ID가 유효하지 않습니다.");
        }
        if (quantityToDecrease <= 0) {
            throw new IllegalArgumentException("감소시킬 수량이 유효하지 않습니다.");
        }

        // 1. 현재 상품 정보 및 재고 확인
        ProductDto productParam = new ProductDto();
        productParam.setSeq(productSeq);
        ProductDto currentProduct = dao.selectOne(productParam); // DAO를 통해 현재 상품 정보 조회

        if (currentProduct == null) {
            System.err.println("### ProductService.decreaseStock - 존재하지 않는 상품 ID: " + productSeq);
            throw new RuntimeException("주문 처리 중 오류가 발생했습니다: 존재하지 않는 상품입니다 (ID: " + productSeq + ")");
        }
        if (currentProduct.getStock() == null || currentProduct.getStock() < quantityToDecrease) {
            System.err.println("### ProductService.decreaseStock - 재고 부족. 상품 ID: " + productSeq +
                               ", 현재 재고: " + currentProduct.getStock() + ", 요청 수량: " + quantityToDecrease);
            throw new RuntimeException(currentProduct.getMealKitName() + " 상품의 재고가 부족합니다. (현재 재고: " + currentProduct.getStock() + ")");
        }

        // 2. 재고 감소 DB 업데이트
        Map<String, Object> params = new HashMap<>();
        params.put("productSeq", productSeq);
        params.put("quantityToDecrease", quantityToDecrease);
        int updatedRows = dao.updateStockDecrease(params); // DAO 호출 (WHERE 절에 재고 >= 감소수량 조건 포함)

        if (updatedRows == 0) {
            // 이 경우는 매우 드물게 발생해야 함 (위에서 이미 재고 확인)
            // 동시성 문제로 인해 그 사이에 재고가 변경되었거나, DAO의 WHERE 조건에 걸린 경우
            System.err.println("### ProductService.decreaseStock - 재고 업데이트 실패 (0 rows affected). 상품 ID: " + productSeq +
                               ". 동시성 문제 또는 DAO의 재고 조건 실패 가능성. 재확인 필요.");
            // 안전하게 한번 더 현재 재고를 확인
            ProductDto refreshedProduct = dao.selectOne(productParam);
            if (refreshedProduct != null && refreshedProduct.getStock() < quantityToDecrease) {
                 throw new RuntimeException(refreshedProduct.getMealKitName() + " 상품의 재고가 부족하여 주문을 완료할 수 없습니다.");
            }
            // 그 외의 이유로 업데이트가 안 되었다면 시스템 오류로 간주
            throw new RuntimeException("알 수 없는 이유로 " + currentProduct.getMealKitName() + " 상품의 재고 업데이트에 실패했습니다.");
        }

        System.out.println("### ProductService.decreaseStock - 재고 감소 성공. 상품 ID: " + productSeq + ", 업데이트된 행 수: " + updatedRows);
        return updatedRows;
    }

    // 기존의 Map을 받던 updateStockDecrease 메서드는 더 이상 외부에서 직접 호출되지 않으므로,
    // 이름을 변경하거나 private/protected로 만들거나, 위 decreaseStock 메서드 내부 로직으로 통합할 수 있습니다.
    // 여기서는 위 decreaseStock 메서드에 로직을 통합했습니다.
    // @Transactional
    // public int updateStockDecrease(Map<String, Object> params) { ... } // 이 메서드는 이제 사용 안 함


    public List<ProductDto> selectFilteredProductList(ProductVo vo) {
        List<ProductDto> list = dao.selectFilteredProductList(vo);
        for (ProductDto dto : list) {
            setBrandNameStringFromCode(dto);
        }
        return list;
    }

    public int selectFilteredProductListCount(ProductVo vo) {
        return dao.selectFilteredProductListCount(vo);
    }
    
    public List<ProductDto> getPopularProducts() {
        ProductVo vo = new ProductVo();
        vo.setShUseNy(1);
        vo.setShDelNy(0);
        // sortOption은 DAO 쿼리에서 직접 지정 (score DESC, reviewCount DESC)
        vo.setRowNumToShow(4); // 예: 인기 상품 4개 표시 (필요에 따라 조절)
        // thisPage는 LIMIT에서 offset 없이 상위 N개만 가져오므로 설정 불필요
        
        // ProductDao.xml의 selectPopularProductList 쿼리가
        // 이미지 경로(path), 리뷰 개수(reviewCount)를 이미 포함하여 조회합니다.
        List<ProductDto> popularList = dao.selectPopularProductList(vo);
        
        for (ProductDto dto : popularList) {
            setBrandNameStringFromCode(dto); // 브랜드명 문자열 채우기
        }
        System.out.println("ProductService - getPopularProducts: " + (popularList != null ? popularList.size() : 0) + " items fetched.");
        return popularList;
    }

    private void setBrandNameStringFromCode(ProductDto dto) {
        if (dto != null && dto.getBrandName() != 0) {
            try {
                String brandNameFromService = codeGroupService.getCodeName("3", String.valueOf(dto.getBrandName()));
                if (brandNameFromService != null && !brandNameFromService.isEmpty()) {
                    dto.setBrandNameAsString(brandNameFromService);
                } else {
                    dto.setBrandNameAsString("브랜드(" + dto.getBrandName() + ")");
                }
            } catch (Exception e) {
                System.err.println("브랜드 이름 조회 중 오류 발생 - 브랜드 코드: " + dto.getBrandName() + ", 오류: " + e.getMessage());
                dto.setBrandNameAsString("정보 조회 오류");
            }
        } else if (dto != null) {
            dto.setBrandNameAsString("브랜드 미지정");
        }
    }
    
    public List<UploadDto> selectListUpload(UploadDto vo) { // ProductController에서 호출
        return uploadDao.selectListUpload(vo);
    }
}