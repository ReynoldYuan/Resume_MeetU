package com.meetu.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.UsersProfile;

import com.meetu.model.UsersProfile;
import com.meetu.model.chatroom.ActChatroomDTO;
import com.meetu.model.chatroom.ChatroomDetail;
import com.meetu.model.chatroom.ChatroomMessage;
import com.meetu.model.chatroom.ChatroomUserDTO;
import com.meetu.model.chatroom.PrivateChatroomDTO;
import com.meetu.service.ChatroomService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class ChatroomController {

	@Autowired
	public ChatroomService cService;
	
	@Value("${domain.url}")
	private String domainUrl;

	@Autowired
	public JsonWebTokenUtility jsonWebTokenUtility;

//	@GetMapping("/secure/users/{userId}/onetoone") 已作廢不用
	public ResponseEntity<?> getUserChatroom(@PathVariable Integer userId,
			@RequestHeader("Authorization") String authHeader) {
		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		// 解析用戶數據
		try {
			JSONObject userJson = new JSONObject(userData);
			Integer loginId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID
			JSONObject response = new JSONObject();

//			先確認查看的使用者和登入者是否相同
			if (loginId != userId) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
			} else {
				// 搜尋一對一聊天室
				List<Map<String, Object>> userOneToOneChatroom = cService.findUserPersonalChatroom(loginId);
				response.put("userOneToOneChatroom", userOneToOneChatroom);

				return ResponseEntity.ok(response.toString());
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}

	@GetMapping("/secure/users/chatroom/onetoone")
	public ResponseEntity<?> getPrivateChatrooms(@RequestHeader("Authorization") String authHeader) {
		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		// 解析用戶數據
		try {
			JSONObject userJson = new JSONObject(userData);
			Integer loginId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID
			List<PrivateChatroomDTO> privateChatrooms = cService.getPrivateChatroomsForUser(loginId);
			return ResponseEntity.ok(privateChatrooms);
		} catch (Exception e) {
			// 記錄錯誤
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/secure/users/chatroom/act")
	public ResponseEntity<?> getActivityChatrooms(@RequestHeader("Authorization") String authHeader) {
		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		// 解析用戶數據
		try {
			JSONObject userJson = new JSONObject(userData);
			Integer loginId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID
			List<ActChatroomDTO> actChatrooms = cService.getActChatroomsForUser(loginId);
			return ResponseEntity.ok(actChatrooms);
		} catch (Exception e) {
			// 記錄錯誤
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/secure/users/{chatroomId}/chatroomHistory")
	public ResponseEntity<?> getChatroomHistory(@PathVariable Integer chatroomId) {
		try {
			List<ChatroomMessage> chatHistory = cService.getChatHistory(chatroomId);
			JSONObject response = new JSONObject();
			JSONArray messageArray = new JSONArray();

			for (ChatroomMessage message : chatHistory) {
				JSONObject messageObject = new JSONObject();
				messageObject.put("messageId", message.getMessageId());
				messageObject.put("chatroomId", message.getChatroomId());
				messageObject.put("senderId", message.getSenderId());
				messageObject.put("content", message.getContent());
				messageObject.put("timestamp", message.getTimestamp().toString());

				messageArray.put(messageObject);
			}
			response.put("chatHistory", messageArray);
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			return ResponseEntity.ok("有異常!");
		}
	}

	@GetMapping("/secure/users/chatroom/{chatroomId}/users")
	public ResponseEntity<List<ChatroomUserDTO>> getChatroomUsers(@PathVariable Integer chatroomId) {
		List<ChatroomUserDTO> users = cService.getChatroomUsersByRoomId(chatroomId);
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
	}

	@GetMapping("/secure/users/chatroom/{chatroomId}/read")
	public ResponseEntity<?> readMsg(@PathVariable Integer chatroomId,
			@RequestHeader("Authorization") String authHeader) {
		try {
			// 驗證 token
			String token = authHeader.substring(7); // 移除 "Bearer " 前綴
			String userData = jsonWebTokenUtility.validateToken(token);

			if (userData == null) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
			}
			JSONObject userJson = new JSONObject(userData);
			Integer loginId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID
			ChatroomDetail savedResult = cService.readAllMessage(chatroomId, loginId);
			JSONObject response = new JSONObject();
			if(savedResult!=null) {
				response.put("readQty", savedResult.getReadQty().equals(null)?0:savedResult.getReadQty());				
			} else {
				response.put("readQty", 0);
			}
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body("Error processing");
		}

	}

	@GetMapping("/secure/users/chatroom/{chatroomId}/memberlist")
	public ResponseEntity<?> getMemberList(@PathVariable Integer chatroomId) {
		try {
			List<UsersProfile> usersProfiles = cService.getUsersProfilesForChatroom(chatroomId);
			JSONArray member = new JSONArray();
			
			for (UsersProfile usersProfile : usersProfiles) {
				JSONObject usersObj = new JSONObject();
				usersObj.put("userId", usersProfile.getUserId());
				usersObj.put("userName", usersProfile.getUserName());
				usersObj.put("userPic", domainUrl+usersProfile.getUserPics());
				
				member.put(usersObj);
			}
			return ResponseEntity.ok(member.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body("Error processing");
	}

}
