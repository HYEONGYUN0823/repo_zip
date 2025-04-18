package com.a7a7.modeule.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.codegroup.CodeGroupService;
import com.a7a7.modeule.codegroup.CodeGroupVo;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


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
	
	
}
