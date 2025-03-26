package com.a7a7.modeule.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.modeule.codegroup.CodeGroupService;

@Controller
public class CodeController {
	@Autowired
	CodeService codeService;
	
//	private void setSearch(CodeVo vo) {
//	    if (vo.getShUseNy() != null && !vo.getShUseNy().equals("")) {
//	        vo.setShUseNy(vo.getShUseNy());
//	    }
//	
//	    if (vo.getShDelNy() != null && !vo.getShDelNy().equals("")) {
//	        vo.setShDelNy(vo.getShDelNy());
//	    }
//	
//	    if (vo.getShOption() != null && vo.getShValue() != null && !vo.getShValue().equals("")) {
//	        vo.setShOption(vo.getShOption());
//	        vo.setShValue(vo.getShValue());
//	    }
//	}
	
	@RequestMapping(value = "/xdm/code/CodeXdmList")
	public String codeXdmList(@ModelAttribute("vo") CodeVo vo, Model model) throws Exception {
//		setSearch(vo); // 검색 조건 설정
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
	public String codeXdmForm(@ModelAttribute("vo") CodeVo vo, Model model) throws Exception{
		System.out.println(codeService.selectTwo(vo));
		if (vo.getIfcdSeq().equals("0") || vo.getIfcdSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", codeService.selectTwo(vo));
		}
		return "xdm/code/CodeXdmForm";
	}
	
	
	@RequestMapping(value = "/xdm/code/CodeXdmInst")
	public String codeXdmInst(CodeDto codeDto) {
		codeService.insert(codeDto);
		
		return "redirect:/xdm/code/CodeXdmList";
	}
	
	@RequestMapping(value = "xdm/code/CodeXdmUpdt")
	public String codeXdmUpdt(CodeDto codeDto) {
		codeService.update(codeDto);
		
		return "redirect:/xdm/code/CodeXdmList";
	}
	
}
