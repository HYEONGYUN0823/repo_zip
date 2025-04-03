package com.a7a7.modeule.codegroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.code.CodeVo;


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
	
	
}
