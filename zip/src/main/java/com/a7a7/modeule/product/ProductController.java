// ProductController.java
package com.a7a7.modeule.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.upload.UploadDto; // ProductDto가 상속하므로 직접 사용은 줄어듬

@Controller
public class ProductController {
    @Autowired
    ProductService productService;

    private void setSearchAndPagingDefaults(ProductVo vo) {
        // ... (이전과 동일)
        vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart().isEmpty() ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
        vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd().isEmpty() ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
        if (vo.getShDelNy() == null) vo.setShDelNy(0);
        if (vo.getShUseNy() == null) vo.setShUseNy(1);
        if (vo.getSortOption() == null || vo.getSortOption().isEmpty()) vo.setSortOption("latest");
        if (vo.getRowNumToShow() == 0) vo.setRowNumToShow(12);
        if (vo.getThisPage() == 0) vo.setThisPage(1);
    }

    @RequestMapping(value = "/usr/product/ProductUsrList")
    public String productList(@ModelAttribute("vo") ProductVo vo, Model model) throws Exception {
        setSearchAndPagingDefaults(vo); // 모든 필터 기본값 및 페이징 설정

        int totalRows = productService.selectFilteredProductListCount(vo);
        vo.setParamsPaging(totalRows);

        List<ProductDto> productList = new ArrayList<>();
        if (totalRows > 0) {
            productList = productService.selectFilteredProductList(vo);
        }

        model.addAttribute("list", productList);
        model.addAttribute("vo", vo);
        return "usr/product/ProductUsrList";
    }

    @RequestMapping(value = "/usr/product/filterProductList", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> filterProductList(
            @ModelAttribute ProductVo vo, // 기본 검색, 페이징, 정렬, 가격 범위는 vo로 받음
            @RequestParam(value = "ratings", required = false) List<Integer> ratings // 평점은 별도 List로 받음
    ) throws Exception {
        vo.setRatings(ratings); // ProductVo에 선택된 평점 리스트 설정
        setSearchAndPagingDefaults(vo); // 나머지 기본값 및 페이징 설정 (thisPage 등은 요청에서 옴)

        int totalRows = productService.selectFilteredProductListCount(vo);
        vo.setParamsPaging(totalRows);

        List<ProductDto> filteredList = new ArrayList<>();
        if (totalRows > 0) {
             filteredList = productService.selectFilteredProductList(vo);
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("products", filteredList);
        responseMap.put("pagingVo", vo);
        return responseMap;
    }
    // ... (XDM 및 ProductUsrView 관련 메서드는 이전 답변과 동일하게 유지)
     @RequestMapping(value = "/xdm/product/ProductXdmList")
     public String productXdmList(@ModelAttribute("vo") ProductVo vo, Model model) throws Exception {
         setSearchAndPagingDefaults(vo);
         int totalRows = productService.selectOneCount(vo);
         vo.setParamsPaging(totalRows);

         if (vo.getTotalRows() > 0) {
             model.addAttribute("list", productService.selectList(vo));
         } else {
             model.addAttribute("list", new ArrayList<ProductDto>());
         }
         return "xdm/product/ProductXdmList";
     }

     @RequestMapping(value = "/xdm/product/ProductXdmForm")
     public String productXdmForm(@ModelAttribute("vo") ProductVo vo, ProductDto dto, Model model) throws Exception {
         if (dto.getSeq() == null || dto.getSeq().isEmpty() || "0".equals(dto.getSeq())) {
             model.addAttribute("item", new ProductDto());
             model.addAttribute("itemFile", new UploadDto());
         } else {
             ProductDto item = productService.selectOne(dto);
             model.addAttribute("item", item);
             if (item != null) {
                 UploadDto uploadVo = new UploadDto();
                 uploadVo.setPseq(item.getSeq());
                 List<UploadDto> fileList = productService.selectListUpload(uploadVo);
                 if (fileList != null && !fileList.isEmpty()) {
                     model.addAttribute("itemFile", fileList.get(0));
                     if(fileList.get(0) != null && fileList.get(0).getPath() != null) {
                         System.out.println("Loaded file path: " + fileList.get(0).getPath());
                     }
                 } else {
                     model.addAttribute("itemFile", new UploadDto());
                 }
             } else {
                  model.addAttribute("itemFile", new UploadDto());
             }
         }
         return "xdm/product/ProductXdmForm";
     }

     @RequestMapping(value = "/xdm/product/ProductXdmInst")
     public String productXdmInst(ProductDto dto, @ModelAttribute("vo") ProductVo vo) throws Exception {
         productService.insert(dto);
         return "redirect:/xdm/product/ProductXdmList" + generateRedirectParams(vo);
     }

     @RequestMapping(value = "/xdm/product/ProductXdmUpdt")
     public String productXdmUpdt(ProductDto dto, @ModelAttribute("vo") ProductVo vo) throws Exception {
         productService.update(dto);
         return "redirect:/xdm/product/ProductXdmList" + generateRedirectParams(vo);
     }

     @RequestMapping(value = "/xdm/product/ProductXdmUele")
     public String productXdmUele(ProductDto dto, @ModelAttribute("vo") ProductVo vo) throws Exception {
         productService.uelete(dto);
         return "redirect:/xdm/product/ProductXdmList" + generateRedirectParams(vo);
     }

     private String generateRedirectParams(ProductVo vo) {
         StringBuilder params = new StringBuilder();
         params.append("?thisPage=").append(vo.getThisPage());
         params.append("&rowNumToShow=").append(vo.getRowNumToShow());
         if(vo.getShValue() != null && !vo.getShValue().isEmpty()) {
             params.append("&shValue=").append(vo.getShValue());
         }
         if(vo.getShOption() != null) {
             params.append("&shOption=").append(vo.getShOption());
         }
         if(vo.getShOption1() != null) {
             params.append("&shOption1=").append(vo.getShOption1());
         }
         return params.toString();
     }
     @RequestMapping(value = "/usr/product/ProductUsrView")
     public String productView(@ModelAttribute("vo") ProductVo urlVo,
                               @RequestParam(value = "seq", required = true) String productSeq, Model model) throws Exception {
         ProductDto dtoForSelect = new ProductDto();
         dtoForSelect.setSeq(productSeq);

         System.out.println("ProductUsrView - Received productSeq: " + dtoForSelect.getSeq());

         ProductDto item = productService.selectOne(dtoForSelect);
         model.addAttribute("item", item);

         if (item != null) {
             UploadDto uploadVo = new UploadDto();
             uploadVo.setPseq(item.getSeq());
             List<UploadDto> fileList = productService.selectListUpload(uploadVo);
             if (fileList != null && !fileList.isEmpty()) {
                 model.addAttribute("itemFile", fileList.get(0));
             } else {
                 model.addAttribute("itemFile", new UploadDto());
             }
         } else {
             model.addAttribute("itemFile", new UploadDto());
         }
         model.addAttribute("vo", urlVo);
         return "usr/product/ProductUsrView";
     }
}