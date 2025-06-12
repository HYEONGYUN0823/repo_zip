package com.a7a7.modeule.blog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class BlogController {
	
	@RequestMapping(value = "/usr/blog/BlogUsrList")
	public String BlogUsrController() {
		
		return "usr/blog/BlogUsrList";
	}
	
	
}
