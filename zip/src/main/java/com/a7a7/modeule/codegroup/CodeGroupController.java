package com.a7a7.modeule.codegroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.modeule.code.CodeService;
import com.a7a7.modeule.code.CodeVo;


@Controller
public class CodeGroupController {
	@Autowired
	CodeGroupService codeGroupService;
	@Autowired
	CodeService codeService;
//	@Autowired
//	CodeVo codeVo;
	
//	private void setSearch(CodeGroupVo vo) {
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
	
	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmList")
	public String CodeGroupXdmList(@ModelAttribute("vo") CodeGroupVo vo, Model model) throws Exception {

//	    setSearch(vo); // 검색 조건 설정
	    vo.setParamsPaging(codeGroupService.selectOneCount());

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
	public String CodeGroupXdmForm(@ModelAttribute("vo") CodeGroupVo vo, CodeVo cvo, Model model) throws Exception{
		
		if (vo.getIfcgSeq().equals("0") || vo.getIfcgSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", codeGroupService.selectOne(vo));
			model.addAttribute("list", codeService.selectList(cvo));
		}
		return "xdm/codegroup/CodeGroupXdmForm";
	}
	
	@RequestMapping(value = "/xdm/codegroup/CodeGroupXdmInst")
	public String CodeGroupXdmInst(CodeGroupDto codeGroupDto) {
		codeGroupService.insert(codeGroupDto);
		
		return "redirect:/xdm/codegroup/CodeGroupXdmList";
	}
	
	
}
