package com.meetu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.Users;
import com.meetu.service.CaptchaGenerator;
import com.meetu.service.UserService;
import com.meetu.util.JsonWebTokenUtility;

@RestController
@CrossOrigin
public class UsersLoginController {
	
	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CaptchaGenerator captchaGenerator;
	
	@PostMapping("/users/loginPost")
	public String loginPost(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String userMail = loginObj.isNull("userMail")?null:loginObj.getString("userMail");
		String userPwd = loginObj.isNull("userPwd")?null:loginObj.getString("userPwd");
		
		if(userMail==null || userMail.length()==0 || userPwd==null || userPwd.length()==0) {
			responseJson.put("message", "請輸入帳號密碼");
			responseJson.put("success", false);
			return responseJson.toString();
		}

		Users result = userService.checkUsersLogin(userMail, userPwd);
		if(result == null) {
			responseJson.put("message", "登入失敗");
			responseJson.put("success", false);
		}
		else if(result.getUserIsBan().equals('D')) {
			responseJson.put("message", "登入失敗，此帳號已被封禁");
			responseJson.put("success", false);
		}
		else if(result.getDeleteState() == (byte)1) {
			responseJson.put("message", "登入失敗，此帳號已被註銷");
			responseJson.put("success", false);
		}
		else {
			responseJson.put("message", "登入成功");
			responseJson.put("success", true);
			
			JSONObject user = new JSONObject();
			user.put("userId", result.getUserId());
			user.put("userMail", result.getUserMail());
			
			String token = jsonWebTokenUtility.createToken(user.toString(), null);
			responseJson.put("token", token);
			responseJson.put("userId", result.getUserId());
			responseJson.put("userMail", result.getUserMail());
			responseJson.put("userPwd", result.getUserPwd());
			responseJson.put("vip", result.getVip());
			responseJson.put("userIsBan", result.getUserIsBan());
			responseJson.put("userName", result.getUsersProfile().getUserName());
			responseJson.put("userGender", result.getUsersProfile().getUserGender());
			responseJson.put("userPics", result.getUsersProfile().getUserPics());
			responseJson.put("userBirth", result.getUsersProfile().getUserBirth());
			responseJson.put("userLocation", result.getUsersProfile().getUserLocation());
			responseJson.put("userJob", result.getUsersProfile().getUserJob());
			responseJson.put("userJobPosi", result.getUsersProfile().getUserJobPosi());
			responseJson.put("userIntroduction", result.getUsersProfile().getUserIntroduction());
			responseJson.put("userPreferAct", result.getUsersProfile().getUserPreferAct());
			responseJson.put("userPreferGen", result.getUsersProfile().getUserPreferGen());
			responseJson.put("userFind", result.getUsersProfile().getUserFind());
			responseJson.put("userPreferAgeMax", result.getUsersProfile().getUserPreferAgeMax());
			responseJson.put("userPreferAgeMin", result.getUsersProfile().getUserPreferAgeMin());
			responseJson.put("userHobby", result.getUsersProfile().getUserHobby());
		}
		return responseJson.toString();
	}
	
	@PostMapping("/users/checkMail")
	public String checkMail(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String userMail = loginObj.isNull("userMail")?null:loginObj.getString("userMail");
		
		if(userMail==null || userMail.length()==0) {
			responseJson.put("message", "請輸入帳號");
			responseJson.put("success", false);
			return responseJson.toString();
		}
		
		boolean exist = userService.checkMailIsExist(userMail);
		
		if(exist) {
			responseJson.put("message", "帳號存在");
			responseJson.put("success", true);
			return responseJson.toString();
		}
		else {
			responseJson.put("message", "帳號不存在");
			responseJson.put("success", false);
			return responseJson.toString();
		}
	}
	
	@PostMapping("/users/checkMailAndCaptcha")
	public String checkMailAndCaptcha(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String userMail = loginObj.isNull("userMail")?null:loginObj.getString("userMail");
		String captcha = loginObj.isNull("captcha")?null:loginObj.getString("captcha");
		
		if(userMail==null || userMail.length()==0 || captcha==null || captcha.length()==0) {
			responseJson.put("message", "請輸入帳號或驗證碼");
			responseJson.put("success", false);
			return responseJson.toString();
		}
		
		boolean exist = userService.checkMailIsExist(userMail);
		boolean correct = captchaGenerator.validateCaptcha(captcha);
		
		if(exist && correct) {
			responseJson.put("message", "帳號驗證成功");
			responseJson.put("success", true);
			return responseJson.toString();
		}
		else if(correct == false){
			responseJson.put("message", "驗證碼輸入錯誤");
			responseJson.put("success", false);
			return responseJson.toString();
		}
		else {
			responseJson.put("message", "帳號不存在");
			responseJson.put("success", false);
			return responseJson.toString();
		}
	}
	
	@PutMapping("/users/checkPassword")
	public String checkPassword(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String userMail = loginObj.getString("userMail");
		String userPwd = loginObj.isNull("userPwd")?null:loginObj.getString("userPwd");
		
		if(userPwd==null || userPwd.length()==0) {
			responseJson.put("message", "請輸入密碼");
			responseJson.put("success", false);
			return responseJson.toString();
		}
		
		boolean updateUserPassword = userService.updateUserPassword(userMail, userPwd);
		
		if(updateUserPassword) {
			responseJson.put("message", "密碼修改成功");
			responseJson.put("success", true);
			return responseJson.toString();
		}
		else {
			responseJson.put("message", "密碼修改失敗");
			responseJson.put("success", false);
			return responseJson.toString();
		}
	}
}
