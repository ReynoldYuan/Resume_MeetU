package com.meetu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.Managers;
import com.meetu.service.ManagerService;
import com.meetu.util.JsonWebTokenUtility;

@RestController
@CrossOrigin
public class ManagersLoginController {
	
	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;
	
	@Autowired
	private ManagerService managerService;
	
	@PostMapping("/managers/login")
	public String login(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String managerEmail = loginObj.isNull("email")?null:loginObj.getString("email");
		String managerPwd = loginObj.isNull("password")?null:loginObj.getString("password");
		
		if(managerEmail==null || managerEmail.length()==0 || managerPwd==null || managerPwd.length()==0) {
			responseJson.put("message", "請輸入帳號密碼");
			responseJson.put("success", false);
			return responseJson.toString();
		}

		Managers result = managerService.checkManagerLogin(managerEmail, managerPwd);
		if(result == null) {
			responseJson.put("message", "登入失敗");
			responseJson.put("success", false);
		}
		else if(result.getDeleteState() == (byte)1) {
			responseJson.put("message", "登入失敗，此帳號已被註銷");
			responseJson.put("success", false);
		}
		else {
			responseJson.put("message", "登入成功");
			responseJson.put("success", true);
			
			JSONObject manager = new JSONObject();
			manager.put("managerId", result.getManagerId());
			manager.put("email", result.getEmail());
			
			String token = jsonWebTokenUtility.createToken(manager.toString(), null);
			responseJson.put("token", token);
			responseJson.put("managerId", result.getManagerId());
			responseJson.put("name", result.getName());
			responseJson.put("picture", result.getPicture());
		}
		return responseJson.toString();
	}
	
	@PostMapping("/managers/checkMail")
	public String checkMail(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String managerMail = loginObj.isNull("email")?null:loginObj.getString("email");
		
		if(managerMail==null || managerMail.length()==0) {
			responseJson.put("message", "請輸入帳號");
			responseJson.put("success", false);
			return responseJson.toString();
		}
		
		boolean exist = managerService.checkEmailIsExist(managerMail);
		
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
	
	@PutMapping("/managers/checkPassword")
	public String checkPassword(@RequestBody String json) throws JSONException {
		JSONObject responseJson = new JSONObject();
		JSONObject loginObj = new JSONObject(json);
		String managerMail = loginObj.getString("email");
		String managerPwd = loginObj.isNull("password")?null:loginObj.getString("password");
		
		if(managerPwd==null || managerPwd.length()==0) {
			responseJson.put("message", "請輸入密碼");
			responseJson.put("success", false);
			return responseJson.toString();
		}
		
		boolean updateUserPassword = managerService.updateManagerPassword(managerMail, managerPwd);
		
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
