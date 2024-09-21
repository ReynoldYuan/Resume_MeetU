package com.meetu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.UsersBan;
import com.meetu.model.UsersBanDTO;
import com.meetu.model.UsersBanId;
import com.meetu.service.UserService;
import com.meetu.service.UsersBanService;
import com.meetu.service.UsersProfileService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class UsersBanController {
	
	@Autowired
	public UserService uService;
	
	@Autowired
	public UsersProfileService uProService;

	@Autowired
	public UsersBanService uBanService;
	
	@Value("${domain.url}")
	private String domainUrl;
	
	@Autowired
	public JsonWebTokenUtility jsonWebTokenUtility;
	
	//封鎖他人
	@PostMapping("/users/ban/")
	public ResponseEntity<?> ban(@RequestHeader("Authorization") String authHeader, @RequestBody String json){
		
		JSONObject jsonObject = new JSONObject(json);
		Integer banedUserId = jsonObject.isNull("banedUserId") ? null : jsonObject.getInt("banedUserId");
		
		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);
		System.out.println(userData);
		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}
		
		try {
			JSONObject userObj = new JSONObject(userData);
			Integer userId = userObj.getInt("userId");
			
			UsersBanId uBanId = new UsersBanId(userId, banedUserId);
			UsersBan ban = uBanService.ban(uBanId);
			
			JSONObject response = new JSONObject();
			if(ban != null) {
				response.put("ifBan", true);
				return ResponseEntity.ok(response.toString());
			}
			response.put("ifBan", false);
			return ResponseEntity.ok(response.toString());
			
		}catch(Exception e){
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}
	
	
	//取消封鎖
	@DeleteMapping("/users/ban/")
	public ResponseEntity<?> unBan(@RequestHeader("Authorization") String authHeader, @RequestBody String json){
		
		JSONObject jsonObject = new JSONObject(json);
		Integer banedUserId = jsonObject.isNull("banedUserId") ? null : jsonObject.getInt("banedUserId");
		
		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);
		
		if(userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}
		
		try {
			JSONObject userObj = new JSONObject(userData);
			Integer userId = userObj.getInt("userId");
			
			UsersBanId uBanId = new UsersBanId(userId, banedUserId);
			uBanService.unBan(uBanId);
			
			JSONObject response = new JSONObject();
			response.put("ifBan", false);
			return ResponseEntity.ok(response.toString());
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
		
		
	}
	
	@GetMapping("/users/ban/{userId}")
	public ResponseEntity<?> ifBan(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}
		
		try {
			JSONObject userJson = new JSONObject(userData);
			Integer loginId = userJson.getInt("userId");
			JSONObject response = new JSONObject();
			
			if(loginId==userId) {
				response.put("myProfile", true);
				return ResponseEntity.ok(response.toString());
			} else {
			response.put("myProfile", false);
			boolean ifBan = uBanService.checkBan(loginId, userId);
			boolean ifBannedMe = uBanService.checkBan(userId, loginId);
			response.put("ifBan", ifBan);
			response.put("ifBannedMe", ifBannedMe);
			return ResponseEntity.ok(response.toString());
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
		
	}
	
	@GetMapping("/users/ban/list")
    public ResponseEntity<?> getBanList(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // 移除 "Bearer " 前綴
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }
        
        try {
            JSONObject userObj = new JSONObject(userData);
            Integer userId = userObj.getInt("userId");
            
            //新增: 將userPic也放入回傳值中
            List<UsersBanDTO> banList = uBanService.getBanList(userId);
            List<Map<String, Object>> response = new ArrayList<>();
            for (UsersBanDTO usersBanDTO : banList) {
            	Map<String, Object> userMap = new HashMap<>();
            	userMap.put("userId", usersBanDTO.getUserId());
            	userMap.put("oppositeId", usersBanDTO.getBanedUserId());
            	userMap.put("userName", usersBanDTO.getBanedUserName());
            	userMap.put("userPic", domainUrl+uProService.findByUserId(usersBanDTO.getBanedUserId()).getUserPics());
            	
            	response.add(userMap);
            	System.out.println("response:"+response);
			}
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing user data");
        }
    }
	
}
