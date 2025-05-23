package com.a7a7.modeule.product;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.codegroup.CodeGroupDto;
import com.a7a7.modeule.member.MemberVo;
import com.a7a7.modeule.upload.UploadDto;
import com.a7a7.modeule.upload.UploadService;

import jakarta.servlet.http.HttpServletResponse;



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
		// 상품 정보
		model.addAttribute("item", productService.selectOne(productDto));

		// 이미지 정보
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
//			    // 0. 사용 여부
////			    cell = row.createCell(1);
////			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
////			    cell.setCellStyle(cellStyle);
////			    cell.setCellValue(Integer.parseInt(list.get(i).getSeq()));
////			    }
//
//			    // 1. 코드그룹 코드
//			    cell = row.createCell(1);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getBrandName());
//
//			    // 2. 코드그룹 이름
//			    cell = row.createCell(2);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getStock());
//
//			    // 3. 코드 이름
//			    cell = row.createCell(3);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getPrice());
//			    
//			    // 3. 코드 이름
//			    cell = row.createCell(3);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    cell.setCellValue(list.get(i).getScore());
//
//			    // 4. 삭제 여부
//			    cell = row.createCell(4);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    if (list.get(i).getMealDelNy() != null) {
//			        cell.setCellValue("0".equals(list.get(i).getMealDelNy()) ? "N" : "Y");
//			    }
//
//			    // 5. 등록일
//			    cell = row.createCell(5);
//			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
//			    cell.setCellStyle(cellStyle);
//			    if (list.get(i).getMealRegDateTime() != null) {
//			    	cell.setCellValue(list.get(i).getMealRegDateTime());
//			    }
//
//			    // 6. 수정일
//			    cell = row.createCell(6);
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
