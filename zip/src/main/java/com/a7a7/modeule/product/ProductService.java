package com.a7a7.modeule.product;

import java.util.List;
import java.util.Map; // HashMap 사용 시 필요 (decreaseStock에서 사용 중)
import java.util.HashMap; // decreaseStock에서 사용 중

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.a7a7.modeule.codegroup.CodeGroupService;
import com.a7a7.modeule.upload.UploadDao;
import com.a7a7.modeule.upload.UploadDto; // UploadDto 임포트 추가
import com.amazonaws.services.s3.AmazonS3Client;

@Service
public class ProductService extends com.a7a7.modeule.upload.UploadService { // FQCN 사용
    @Autowired
    ProductDao dao;
    @Autowired
    CodeGroupService codeGroupService;

    @Autowired
    UploadDao uploadDao;
    @Autowired
    AmazonS3Client amazonS3Client;

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
        int result = dao.insert(dto);
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
        return result;
    }

    @Transactional
    public int update(ProductDto productDto) {
        // TODO: 파일 관련 로직 (업데이트 시 기존 파일 처리 등)
        return dao.update(productDto);
    }

    @Transactional
    public int uelete(ProductDto productDto) {
        // TODO: 관련 파일 삭제 로직 (논리적 또는 물리적)
        return dao.uelete(productDto);
    }

    @Transactional
    public int updateStockDecrease(Map<String, Object> params) {
         ProductDto productParam = new ProductDto();
         productParam.setSeq((String)params.get("productSeq"));
         ProductDto currentProduct = dao.selectOne(productParam);

         if (currentProduct == null) {
             throw new RuntimeException("존재하지 않는 상품입니다. (ID: " + params.get("productSeq") + ")");
         }
         Integer quantityToDecrease = (Integer) params.get("quantityToDecrease");
         if (currentProduct.getStock() == null || currentProduct.getStock() < quantityToDecrease) {
             throw new RuntimeException("재고가 부족합니다. (상품: " + currentProduct.getMealKitName() + ")");
         }
         int updatedRows = dao.updateStockDecrease(params);
         if (updatedRows == 0) {
              ProductDto refreshedProduct = dao.selectOne(productParam);
             if (refreshedProduct.getStock() < quantityToDecrease) { // refreshedProduct null 체크 추가
                  throw new RuntimeException("재고가 부족하여 업데이트되지 않았습니다. (상품: " + (refreshedProduct != null ? refreshedProduct.getMealKitName() : "알수없음") + ")");
             }
             throw new RuntimeException("알 수 없는 이유로 재고 업데이트에 실패했습니다. 상품 ID: " + params.get("productSeq"));
         }
         return updatedRows;
    }

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

    // 브랜드 코드에 해당하는 이름을 설정하는 헬퍼 메서드 (이 메서드 하나만 남깁니다)
    private void setBrandNameStringFromCode(ProductDto dto) {
        if (dto != null && dto.getBrandName() != 0) {
            try {
                // CodeGroupService의 getCodeName 메서드 호출 (파라미터 타입 및 순서 확인)
                String brandNameFromService = codeGroupService.getCodeName("3", String.valueOf(dto.getBrandName()));

                if (brandNameFromService != null && !brandNameFromService.isEmpty()) {
                    dto.setBrandNameAsString(brandNameFromService);
                } else {
                    dto.setBrandNameAsString("브랜드(" + dto.getBrandName() + ")"); // 코드를 찾지 못한 경우
                }
            } catch (Exception e) {
                System.err.println("브랜드 이름 조회 중 오류 발생 - 브랜드 코드: " + dto.getBrandName() + ", 오류: " + e.getMessage());
                dto.setBrandNameAsString("정보 조회 오류"); // 오류 발생 시
            }
        } else if (dto != null) {
            dto.setBrandNameAsString("브랜드 미지정"); // brandName 코드가 0이거나 없는 경우
        }
    }
    
    public List<UploadDto> selectListUpload(UploadDto vo) {
        return uploadDao.selectListUpload(vo);
    }
}