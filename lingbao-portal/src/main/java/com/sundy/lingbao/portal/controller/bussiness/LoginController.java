package com.sundy.lingbao.portal.controller.bussiness;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping("/")
	public String login() {
		return "redirect:/index.html";
	}
	
}
