package com.a7a7.modeule.blog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class BlogController {
	
	@RequestMapping(value = "/usr/blog/BlogUsrList")
	public String BlogUsrList() {
		
		return "usr/blog/BlogUsrList";
	}
	
	@RequestMapping(value = "/usr/blog/BlogUsrView")
	public String BlogUsrView() {
		
		return "usr/blog/BlogUsrView";
	}
	
	
}
