package com.a7a7.modeule.code;

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
import com.a7a7.modeule.codegroup.CodeGroupService;
import com.a7a7.modeule.codegroup.CodeGroupVo;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class CodeController {
	@Autowired
	CodeService codeService;
	@Autowired
	CodeGroupService codeGroupService; 
	
	private void setSearch(CodeVo vo) {
		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
	}
	
	@RequestMapping(value = "/xdm/code/CodeXdmList")
	public String codeXdmList(@ModelAttribute("vo") CodeVo vo, Model model) throws Exception {
		setSearch(vo); // 검색 조건 설정
	    // rowNumToShow 기본값 처리
	    if (vo.getRowNumToShow() <= 0) {
	        vo.setRowNumToShow(5);
	    }
		vo.setParamsPaging(codeService.selectOneCount(vo));
		

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", codeService.selectList(vo));
	    }
		
		return "xdm/code/CodeXdmList";
	}
	
//	@RequestMapping(value = "/xdm/code/CodeXdmView")
//	public String requestMethodName(Model model, CodeDto codeDto) {
//		model.addAttribute("item", codeService.selectOne(codeDto));
//		
//		return "xdm/code/CodeXdmView";
//	}
	
	
	@RequestMapping(value = "/xdm/code/CodeXdmForm")
	public String codeXdmForm(@ModelAttribute("vo") CodeVo vo, CodeGroupVo gvo, CodeDto codeDto, Model model) throws Exception{
		System.out.println(codeService.selectTwo(codeDto));
		
		model.addAttribute("listCodeGroup", codeGroupService.selectListWithoutPaging()); 
		if (vo.getIfcdSeq().equals("0") || vo.getIfcdSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", codeService.selectTwo(codeDto));
		}
		return "xdm/code/CodeXdmForm";
	}
	
	
	@RequestMapping(value = "/xdm/code/CodeXdmInst")
	public String codeXdmInst(CodeDto codeDto) {
		codeService.insert(codeDto);
		
		return "redirect:/xdm/code/CodeXdmList";
	}
	
	@RequestMapping(value = "/xdm/code/CodeXdmUpdt")
	public String codeXdmUpdt(CodeDto codeDto) {
		codeService.update(codeDto);
		
		return "redirect:/xdm/code/CodeXdmList";
	}
	
	@RequestMapping(value = "/xdm/code/CodeXdmUele")
	public String codeXdmUele(CodeDto codeDto) {
		codeService.uelete(codeDto);
		
		return "redirect:/xdm/code/CodeXdmList";
	}
	
	@RequestMapping("/excelDownload")
    public void excelDownload(CodeVo vo, HttpServletResponse httpServletResponse) throws Exception {
	    
		setSearch(vo);
		vo.setParamsPaging(codeService.selectOneCount(vo));

		if (vo.getTotalRows() > 0) {
			List<CodeDto> list = codeService.selectList(vo);
			
			Workbook workbook = new HSSFWorkbook();	// for xls
//	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("첫번째 시트");
	        CellStyle cellStyle = workbook.createCellStyle();        
	        Row row = null;
	        Cell cell = null;
	        int rowNum = 0;
			
//	        each column width setting
	        sheet.setColumnWidth(0, 2100);
	        sheet.setColumnWidth(1, 3100);

//	        Header
	        String[] tableHeader = {"사용", "코드그룹 코드", "코드그룹 이름", "코드 이름", "삭제", "등록일", "수정일"};

	        row = sheet.createRow(rowNum++);
			for(int i=0; i<tableHeader.length; i++) {
				cell = row.createCell(i);
	        	cellStyle.setAlignment(HorizontalAlignment.CENTER);
	        	cell.setCellStyle(cellStyle);
				cell.setCellValue(tableHeader[i]);
			}

//	        Body
			for (int i = 0; i < list.size(); i++) {
			    row = sheet.createRow(rowNum++);

			    // 0. 사용 여부
			    cell = row.createCell(0);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcdUseNy() != null) {
			        cell.setCellValue("0".equals(list.get(i).getIfcdUseNy()) ? "N" : "Y");
			    }

			    // 1. 코드그룹 코드
			    cell = row.createCell(1);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    cell.setCellValue(Integer.parseInt(list.get(i).getIfcgSeq()));

			    // 2. 코드그룹 이름
			    cell = row.createCell(2);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    cell.setCellValue(list.get(i).getIfcgName());

			    // 3. 코드 이름
			    cell = row.createCell(3);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    cell.setCellValue(list.get(i).getIfcdName());

			    // 4. 삭제 여부
			    cell = row.createCell(4);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcdDelNy() != null) {
			        cell.setCellValue("0".equals(list.get(i).getIfcdDelNy()) ? "N" : "Y");
			    }

			    // 5. 등록일
			    cell = row.createCell(5);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcdRegDateTime() != null) {
			    	cell.setCellValue(list.get(i).getIfcdRegDateTime());
			    }

			    // 6. 수정일
			    cell = row.createCell(6);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcdModDateTime() != null) {
			        cell.setCellValue(list.get(i).getIfcdModDateTime());
			    }
			}

	        httpServletResponse.setContentType("ms-vnd/excel");
	        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=example.xls");	// for xls
//	        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

	        workbook.write(httpServletResponse.getOutputStream());
	        workbook.close();
		}
    }







	
}
