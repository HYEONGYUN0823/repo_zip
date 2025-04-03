package com.a7a7.modeule.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.util.UtilDateTime;



@Controller
public class ProductController {
	@Autowired
	ProductService productService;
	
	private void setSearch(ProductVo vo) {
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
	public String productXdmForm(@ModelAttribute("vo") ProductVo vo, ProductDto productDto, Model model) throws Exception{
		
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", productService.selectOne(productDto));
		}
		return "xdm/product/ProductXdmForm";
	}
	
	@RequestMapping(value = "/xdm/product/ProductXdmInst")
	public String productXdmInst(ProductDto productDto) {
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
	
	
}
