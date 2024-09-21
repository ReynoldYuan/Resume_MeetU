package com.meetu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetu.model.Users;
import com.meetu.service.UserService;

@RestController
@CrossOrigin
public class UsersRestController {

	@Autowired
	private UserService userService;
	
	@PostMapping("/users/profile")
	public String registerPost(@RequestPart String userJson, @RequestPart MultipartFile userPics) throws IOException {
		Users user = new ObjectMapper().readValue(userJson, Users.class);
		boolean exist = userService.checkMailIsExist(user.getUserMail());
		
		if(!exist) {
			boolean register = userService.registerUser(user, userPics);
			if(register) {
				return "註冊成功";
			}
			else {
				return "註冊失敗";
			}
		}
		else {
			return "已經有此帳號，請重新輸入";
		}
	}
	
	@GetMapping("/users/profile")
	public List<Users> showAll() {
		return userService.showAllUsers();
	}
	
	@GetMapping("/users/profile/{userId}")
	public Users show(@PathVariable Integer userId) {
		return userService.showUser(userId);
	}
	
	@PutMapping("/users/profile")
	public String updatePost(@RequestPart String userJson, @RequestPart MultipartFile userPics) throws IOException {
		Users user = new ObjectMapper().readValue(userJson, Users.class);
		boolean update = userService.updateUserProfile(user, userPics);
		if(update) {
			return "修改成功";
		}
		else {
			return "修改失敗";
		}
	}
	
	@DeleteMapping("/users/profile/{userId}")
	public String delete(@PathVariable Integer userId) {
		Users user = userService.showUser(userId);
		if(user != null) {
			boolean deleteUser = userService.deleteUser(user);
			if(deleteUser)
				return "刪除成功";
			else
				return "刪除失敗";
		}
		else
			return "無此使用者";
	}
	
	@DeleteMapping("/users/profile/recover/{userMail}")
	public String recoverByManager(@PathVariable String userMail) {
		boolean result = userService.recoverUserByManager(userMail);
		if(result) {
			return "恢復成功";
		}
		else
			return "無法恢復，此使用者並無註銷或被停權";
	}
	
}
