package com.meetu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.meetu.model.Users;
import com.meetu.service.UserService;


@Controller
public class UsersController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users/login")
	public String login() {
		return "/users/loginPage";
	}
	
	@GetMapping("/users/register")
	public String register() {
		return "/users/registerPage";
	}
	
	@GetMapping("/users/update/{userId}")
	public String update(@PathVariable Integer userId, Model model) {
		Users user = userService.showUser(userId);
		model.addAttribute("userUpdate", user);
		return "/users/updatePage";
	}
	
}
