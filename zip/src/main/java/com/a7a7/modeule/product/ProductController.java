package com.a7a7.modeule.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.member.MemberVo;
import com.a7a7.modeule.upload.UploadDto;
import com.a7a7.modeule.upload.UploadService;



@Controller
public class ProductController {
	@Autowired
	ProductService productService;
	@Autowired
	UploadService uploadService;
	
	private void setSearch(ProductVo vo) {
		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
	}
//	-----
	private void setSearchProduct(ProductListVo vo) {
		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
	}
	
	@RequestMapping(value = "/xdm/product/ProductXdmList")
	public String productXdmList(@ModelAttribute("vo") ProductVo vo, ProductDto productDto, Model model) throws Exception {

	    setSearch(vo); // 검색 조건 설정
	    vo.setParamsPaging(productService.selectOneCount(vo));

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", productService.selectList(vo));
	        model.addAttribute("productDto", productDto);
	        System.out.println("shOption1: " + vo.getShOption1());
	    }
	    
		return "xdm/product/ProductXdmList";
	}
	
	@RequestMapping(value = "/xdm/product/ProductXdmForm")
	public String productXdmForm(@ModelAttribute("vo") ProductVo vo, ProductDto productDto, UploadDto uploadDto, Model model) throws Exception {
		
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			uploadDto.setPseq(productDto.getSeq());
			UploadDto dto = uploadService.selectOne(uploadDto);
			System.out.println(dto.getPath());
			
			model.addAttribute("item", productService.selectOne(productDto));
			model.addAttribute("itemFile", dto);
		}
		return "xdm/product/ProductXdmForm";
	}
	
	@RequestMapping(value = "/xdm/product/ProductXdmInst")
	public String productXdmInst(ProductDto productDto) throws Exception {
		productService.insert(productDto);
		
		
		
		return "redirect:/xdm/product/ProductXdmList";
	}
	
	@RequestMapping(value = "/xdm/product/ProductXdmUpdt")
	public String productXdmUpdt(ProductDto productDto) {
		productService.update(productDto);
		
		return "redirect:/xdm/product/ProductXdmList";
	}
	
	@RequestMapping(value = "/xdm/product/ProductXdmUele")
	public String productXdmUele(ProductDto productDto) {
		productService.uelete(productDto);
		
		return "redirect:/xdm/product/ProductXdmList";
	}
	
	/* #######################################################
	 					웹사이트
	 ########################################################*/
	
	@RequestMapping(value = "/usr/product/ProductUsrList")
	public String productList(@ModelAttribute("vo") ProductListVo vo, @ModelAttribute("productVo") ProductVo productVo, ProductDto productDto, UploadDto uploadDto, Model model) throws Exception {
		setSearchProduct(vo);
		vo.setParamsPaging(productService.selectOneCount(productVo));
		uploadDto.setPseq(productDto.getSeq());
		UploadDto dto = uploadService.selectOne(uploadDto);
		model.addAttribute("list", productService.selectProductList(vo));
		model.addAttribute("item", productService.selectOne(productDto));
		model.addAttribute("itemFile", dto);
		
		return "usr/product/ProductUsrList";
	}
	
	@RequestMapping(value = "/usr/product/ProductUsrView")
	public String productView(@ModelAttribute("vo") MemberVo vo, ProductDto productDto, UploadDto uploadDto, Model model) throws Exception {
	    System.out.println("ProductDto seq: " + productDto.getSeq()); // 디버깅 로그
	    if (productDto.getSeq() != null && productDto.getSeq().contains(",")) {
	        productDto.setSeq(productDto.getSeq().split(",")[0]); // 첫 번째 값만 사용
	    }
	    model.addAttribute("item", productService.selectOne(productDto));
	    uploadDto.setPseq(productDto.getSeq());
	    UploadDto itemFile = uploadService.selectOne(uploadDto);
	    model.addAttribute("itemFile", itemFile);
	    return "usr/product/ProductUsrView";
	}
	
//	@RequestMapping("/xdm/product/excelDownload")
//    public void excelDownload(ProductVo pvo, ProductListVo vo, HttpServletResponse httpServletResponse) throws Exception {
//	    
//		setSearch(pvo);
//		vo.setParamsPaging(productService.selectOneCount(pvo));
//
//		if (vo.getTotalRows() > 0) {
//			List<ProductDto> list = productService.selectProductList(vo);
//			
//			Workbook workbook = new HSSFWorkbook();	// for xls
////	        Workbook workbook = new XSSFWorkbook();
//	        Sheet sheet = workbook.createSheet("첫번째 시트");
//	        CellStyle cellStyle = workbook.createCellStyle();        
//	        Row row = null;
//	        Cell cell = null;
//	        int rowNum = 0;
//			
////	        each column width setting
//	        sheet.setColumnWidth(0, 2100);
//	        sheet.setColumnWidth(1, 3100);
//
////	        Header
//	        String[] tableHeader = {"코드", "브랜드", "밀키트 이름", "재고", "가격", "평점", "사용", "등록일", "수정일"};
//
//	        row = sheet.createRow(rowNum++);
//			for(int i=0; i<tableHeader.length; i++) {
//				cell = row.createCell(i);
//	        	cellStyle.setAlignment(HorizontalAlignment.CENTER);
//	        	cell.setCellStyle(cellStyle);
//				cell.setCellValue(tableHeader[i]);
//			}
//
////	        Body
//			for (int i = 0; i < list.size(); i++) {
//			    row = sheet.createRow(rowNum++);
//
//			    // 0. 코드
////			    cell = row.createCell(0);
////			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
////			    cell.setCellStyle(cellStyle);
////			    cell.setCellValue(Integer.parseInt(list.get(i).getSeq()));
////			    }
//
//			    // 1. 브랜드
//			    cell = row.createCell(1);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getBrandName());
//
//			    // 2. 밀키트 이름
//			    cell = row.createCell(2);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getMealKitName());
//
//			    // 3. 재고
//			    cell = row.createCell(3);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getStock());
//
//			    // 4. 가격
//			    cell = row.createCell(4);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getPrice());
//			    
//			    // 5. 평점
//			    cell = row.createCell(5);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getScore());
//
//			    // 6. 사용 여부
//			    cell = row.createCell(6);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    if (list.get(i).getMealDelNy() != null) {
//			        cell.setCellValue("0".equals(list.get(i).getMealDelNy()) ? "N" : "Y");
//			    }
//
//			    // 7. 등록일
//			    cell = row.createCell(7);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    if (list.get(i).getMealRegDateTime() != null) {
//			    	cell.setCellValue(list.get(i).getMealRegDateTime());
//			    }
//
//			    // 8. 수정일
//			    cell = row.createCell(8);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    if (list.get(i).getMealModDateTime() != null) {
//			        cell.setCellValue(list.get(i).getMealModDateTime());
//			    }
//			}
//
//	        httpServletResponse.setContentType("ms-vnd/excel");
//	        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=example.xls");	// for xls
////	        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=example.xlsx");
//
//	        workbook.write(httpServletResponse.getOutputStream());
//	        workbook.close();
//		}
//    }
	
}
