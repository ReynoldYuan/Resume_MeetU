package com.meetu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BackendController {
	
	@GetMapping("/backend")
	public String backend() {
		return "backend/index";
	}
	

}
