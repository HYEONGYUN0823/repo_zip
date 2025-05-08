package com.a7a7.modeule.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.member.MemberDto;
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
		
		return "usr/product/ProductList";
	}
	
	@RequestMapping(value = "/usr/product/ProductView")
	public String productView(@ModelAttribute("vo") MemberVo vo, ProductDto productDto, Model model) throws Exception {
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", productService.selectOne(productDto));
		}
		
		return "usr/product/ProductView";
	}
	
//	@GetMapping("/product/filter")
//	@ResponseBody
//	public List<ProductDto> filterProducts(@RequestParam int minPrice,
//	                                       @RequestParam int maxPrice,
//	                                       @RequestParam List<Integer> ratings) {
//	    return productService.filterProducts(minPrice, maxPrice, ratings);
//	}
	
	
}
