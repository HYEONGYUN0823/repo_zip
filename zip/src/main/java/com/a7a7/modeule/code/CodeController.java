package com.a7a7.modeule.code;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	@RequestMapping("/xdm/code/excelDownload")
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



	@RequestMapping(value = "/xdm/code/readExcel")
	public String readExcel(CodeDto dto, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		// ... (파일 유효성 검사 부분은 동일)

		HSSFWorkbook workbook = null;
        // ...

		try {
            workbook = new HSSFWorkbook(file.getInputStream());
		    HSSFSheet worksheet = workbook.getSheetAt(0);
            int lastRowNum = worksheet.getLastRowNum();
		    DataFormatter formatter = new DataFormatter();

		    for(int i=1; i <= lastRowNum ;i++) {
			    CodeDto excel = new CodeDto();
		        HSSFRow row = worksheet.getRow(i);

                if (row == null) {
                    continue;
                }

                try {
                    // =====================================================================
                    // !!!!! 실제 엑셀 파일 컬럼 순서에 맞게 수정 !!!!!
                    // =====================================================================
                    // 예시: A열(0)=사용여부, B열(1)=코드그룹ID, C열(2)=코드그룹명, D열(3)=코드명, E열(4)=삭제여부, F열(5)=등록일, G열(6)=수정일
                    // ★★★ ifcdSeq (코드 ID)는 엑셀에 없다고 가정 ★★★
                    String useNyFromExcel       = formatter.formatCellValue(row.getCell(0)); // 사용여부 (A열)
                    String ifcgSeqStrFromFile   = formatter.formatCellValue(row.getCell(1)); // 코드그룹 ID (B열, 숫자여야 함)
                    // String ifcgNameFromFile  = formatter.formatCellValue(row.getCell(2)); // 코드그룹명 (C열)
                    String ifcdNameFromFile     = formatter.formatCellValue(row.getCell(3)); // 코드명 (D열)
                    String delNyFromExcel       = formatter.formatCellValue(row.getCell(4)); // 삭제 여부 (E열, 'N' 또는 'Y')
                    String regDate              = formatter.formatCellValue(row.getCell(5)); // 등록일 (F열) - 주신 정보 반영
                    String modDate              = formatter.formatCellValue(row.getCell(6)); // 수정일 (G열) - 주신 정보 반영
                    // =====================================================================


                    // 필수 ID 값 (ifcgSeq) 읽기 및 숫자 변환 검사
                    if (ifcgSeqStrFromFile == null || ifcgSeqStrFromFile.trim().isEmpty()) {
                        System.err.println("Skipping row " + (i + 1) + ": 필수 값(코드그룹ID) 누락.");
                        continue;
                    }

                    try {
                        Integer.parseInt(ifcgSeqStrFromFile.trim()); // 코드그룹 ID가 숫자인지 확인
                        // ifcdSeq는 엑셀에 없으므로 여기서 숫자 변환 검사 안 함
                    } catch (NumberFormatException nfe) {
                        System.err.println("Skipping row " + (i + 1) + ": ifcgSeq ('" + ifcgSeqStrFromFile + "') is not a valid integer.");
                        continue;
                    }

                    // useNy 처리: 'N' -> "0", 그 외 -> "1"
                    String useNyProcessed = "N".equalsIgnoreCase(useNyFromExcel) ? "0" : "1";

                    // delNy 처리: 'N' -> "0", 그 외 -> "1"
                    String delNyProcessed = "N".equalsIgnoreCase(delNyFromExcel) ? "0" : "1";

                    // DTO에 값 설정
			        excel.setIfcgSeq(ifcgSeqStrFromFile.trim());
                    // excel.setIfcdSeq(...); // ★★★ ifcdSeq는 DB에서 자동 생성되므로 DTO에 설정 안 함 ★★★
			        excel.setIfcdName(ifcdNameFromFile != null ? ifcdNameFromFile.trim() : null);
			        excel.setIfcdUseNy(useNyProcessed);
                    excel.setIfcdDelNy(delNyProcessed);
	                excel.setIfcdRegDateTime(regDate != null ? regDate.trim() : null);
	                excel.setIfcdModDateTime(modDate != null ? modDate.trim() : null);

                    codeService.insert(excel);

                } catch (Exception e) {
                    System.err.println("Error processing row " + (i + 1) + ": " + e.getMessage());
                }
		    }
            redirectAttributes.addFlashAttribute("uploadMessage", "엑셀 파일 처리를 시도했습니다. (결과는 서버 로그 확인)");

		} catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("uploadMessage", "엑셀 파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            if (workbook != null) {
                try { workbook.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
		return "redirect:/xdm/code/CodeXdmList";
	}



	
}
