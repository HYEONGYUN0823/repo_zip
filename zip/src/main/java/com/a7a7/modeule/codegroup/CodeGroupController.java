package com.a7a7.modeule.codegroup;

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
import com.a7a7.modeule.code.CodeVo;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class CodeGroupController {
	@Autowired
	CodeGroupService codeGroupService;
	
	private void setSearch(CodeGroupVo vo) {
		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
	}
	
	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmList")
	public String codeGroupXdmList(@ModelAttribute("vo") CodeGroupVo vo, Model model) throws Exception {

	    setSearch(vo); // 검색 조건 설정
	    vo.setParamsPaging(codeGroupService.selectOneCount(vo));

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", codeGroupService.selectList(vo));
	    }

	    return "xdm/codegroup/CodeGroupXdmList";
	}
	
//	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmView")
//	public String CodeGroupXdmView(Model model, CodeGroupDto codeGroupDto) {
//		model.addAttribute("item", codeGroupService.selectOne(codeGroupDto));
//		
//		return "xdm/codegroup/CodeGroupXdmView";
//	}
	
	
	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmForm")
	public String codeGroupXdmForm(@ModelAttribute("vo") CodeGroupVo vo, CodeVo cvo, Model model) throws Exception{
		
		if (vo.getIfcgSeq().equals("0") || vo.getIfcgSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", codeGroupService.selectOne(vo));
		}
		return "xdm/codegroup/CodeGroupXdmForm";
	}
	
	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmInst")
	public String codeGroupXdmInst(CodeGroupDto codeGroupDto) {
		codeGroupService.insert(codeGroupDto);
		
		return "redirect:/xdm/codegroup/CodeGroupXdmList";
	}
	
	@RequestMapping(value = "/xdm/codeGroup/CodeGroupXdmUpdt")
	public String codeGroupXdmUpdt(CodeGroupDto codeGroupDto) {
		codeGroupService.update(codeGroupDto);
		
		return "redirect:/xdm/codegroup/CodeGroupXdmList";
	}
	
	@RequestMapping(value = "/xdm/codeGroup/CodeGroupXdmUele")
	public String codeGroupXdmUele(CodeGroupDto codeGroupDto) {
		codeGroupService.uelete(codeGroupDto);
		
		return "redirect:/xdm/codegroup/CodeGroupXdmList";
	}
	
	@RequestMapping("/xdm/codeGroup/excelDownload")
    public void excelDownload(CodeGroupVo vo, HttpServletResponse httpServletResponse) throws Exception {
	    
		setSearch(vo);
		vo.setParamsPaging(codeGroupService.selectOneCount(vo));

		if (vo.getTotalRows() > 0) {
			List<CodeGroupDto> list = codeGroupService.selectList(vo);
			
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
	        String[] tableHeader = {"사용", "코드그룹 코드", "코드그룹 이름", "삭제", "등록일", "수정일"};

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
			    if (list.get(i).getIfcgUseNy() != null) {
			        cell.setCellValue("0".equals(list.get(i).getIfcgUseNy()) ? "N" : "Y");
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

			    // 4. 삭제 여부
			    cell = row.createCell(3);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcgDelNy() != null) {
			        cell.setCellValue("0".equals(list.get(i).getIfcgDelNy()) ? "N" : "Y");
			    }

			    // 5. 등록일
			    cell = row.createCell(4);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcgRegDateTime() != null) {
			    	cell.setCellValue(list.get(i).getIfcgRegDateTime());
			    }

			    // 6. 수정일
			    cell = row.createCell(5);
			    cellStyle.setAlignment(HorizontalAlignment.CENTER);
			    cell.setCellStyle(cellStyle);
			    if (list.get(i).getIfcgModDateTime() != null) {
			        cell.setCellValue(list.get(i).getIfcgModDateTime());
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
