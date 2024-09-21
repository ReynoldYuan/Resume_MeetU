package com.meetu.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.SocialFollow;
import com.meetu.model.SocialFollowId;
import com.meetu.model.UsersBanDTO;
import com.meetu.model.UsersProfile;
import com.meetu.model.UsersProfileRespository;
import com.meetu.service.MatchingService;
import com.meetu.service.SocialFollowService;
import com.meetu.service.UserService;
import com.meetu.service.UsersProfileService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class SocialFollowController {

	@Autowired
	public UserService uService;

	@Autowired
	public SocialFollowService sfService;

	@Autowired
	public UsersProfileService upService;

	@Autowired
	public MatchingService mService;

	@Value("${domain.url}")
	private String domainUrl;

	@Autowired
	public JsonWebTokenUtility jsonWebTokenUtility;

	@GetMapping("/users/{userId}")
	public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
		UsersProfile usersProfile = upService.findByUserId(userId);
		if (usersProfile == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errorMsg", "無此會員，請重新輸入"));
		}

		if (usersProfile.getUsers().getUserIsBan() == 'D') {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errorMsg", "無此會員，請重新輸入"));
		}

		Long followeeNum = sfService.countFollowee(userId);
		Long followerNum = sfService.countFollower(userId);
//		Integer mutualFollow = sfService.countMutualFollow(userId);
		Integer mutualMatchingQty = mService.countMutualMatching(userId);

		// 還有活動數量待補

		Map<String, Object> response = new HashMap<>();
		response.put("userId", usersProfile.getUserId());
		response.put("userName", usersProfile.getUserName());
		response.put("userGender", String.valueOf(usersProfile.getUserGender()));
		response.put("userPics", domainUrl + usersProfile.getUserPics());
		response.put("userBirth", usersProfile.getUserBirth());
		response.put("userLocation", usersProfile.getUserLocation());
		response.put("userJob", usersProfile.getUserJob());
		response.put("userJobPosi", usersProfile.getUserJobPosi());
		response.put("userIntroduction", usersProfile.getUserIntroduction());
		response.put("userPreferAct", String.valueOf(usersProfile.getUserPreferAct()));
		response.put("userPreferGen", String.valueOf(usersProfile.getUserPreferGen()));
		response.put("userFind", String.valueOf(usersProfile.getUserFind()));
		response.put("userHobby", usersProfile.getUserHobby());
		response.put("userPreferAgeMin", usersProfile.getUserPreferAgeMin());
		response.put("userPreferAgeMax", usersProfile.getUserPreferAgeMax());
		response.put("followeeNum", followeeNum != null ? followeeNum : 0);
		response.put("followerNum", followerNum != null ? followerNum : 0);
//		response.put("mutualFollow", mutualFollow != null ? mutualFollow : 0);
		response.put("mutualMatchingQty", mutualMatchingQty != null ? mutualMatchingQty : 0);

		return ResponseEntity.ok(response);
	}

	// 查看是否追蹤該使用者，且同步確認查看的使用者是否為登入者自己
	@GetMapping("/secure/users/follow/{userId}")
	public ResponseEntity<?> ifFollow(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
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

//			先確認查看的使用者和登入者是否相同，若相同則讓畫面出現修改/刪除按鈕，和顯示機密個人資訊
			if (loginId == userId) {
				response.put("myProfile", true);
				return ResponseEntity.ok(response.toString());
			} else {
				// 執行檢查關注狀態的邏輯
				response.put("myProfile", false);
				boolean ifFollowing = sfService.checkIfFollow(loginId, userId);
				response.put("ifFollowing", ifFollowing);
				return ResponseEntity.ok(response.toString());
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}

	// 追蹤他人
	@PostMapping("/secure/users/follow/")
	public ResponseEntity<?> follow(@RequestHeader("Authorization") String authHeader, @RequestBody String json) {
		JSONObject obj = new JSONObject(json);
		Integer followeeId = obj.isNull("followeeId") ? null : obj.getInt("followeeId");

		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		// 解析用戶數據
		try {
			JSONObject userJson = new JSONObject(userData);
			Integer followerId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID
			SocialFollowId sfId = new SocialFollowId(followerId, followeeId);
			SocialFollow socialFollow = sfService.follow(sfId);
			JSONObject response = new JSONObject();
			if (socialFollow != null) {
				response.put("ifFollowing", true);
				return ResponseEntity.ok(response.toString());
			}
			response.put("ifFollowing", false);
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}

	// 取消追蹤
	@DeleteMapping("/secure/users/follow/")
	public ResponseEntity<?> unfollow(@RequestHeader("Authorization") String authHeader, @RequestBody String json) {
		JSONObject obj = new JSONObject(json);
		Integer followeeId = obj.isNull("followeeId") ? null : obj.getInt("followeeId");

		// 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		// 解析用戶數據
		try {
			JSONObject userJson = new JSONObject(userData);
			Integer followerId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID
			SocialFollowId sfId = new SocialFollowId(followerId, followeeId);
			sfService.unfollow(sfId);
			JSONObject response = new JSONObject();
			response.put("ifFollowing", false);
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}

	// 取得好友資料
	@GetMapping("/secure/users/mutualmatching")
	public ResponseEntity<?> getMutualMatchingList(@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		try {
			JSONObject userObj = new JSONObject(userData);
			Integer userId = userObj.getInt("userId");

			List<UsersProfile> mutualMatchingList = mService.getMutualMatchingList(userId);
			List<Map<String, Object>> response = new ArrayList<>();
			if (mutualMatchingList!=null) {
				for (UsersProfile usersProfile : mutualMatchingList) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("oppositeId", usersProfile.getUserId());
					userMap.put("userName", usersProfile.getUserName());
					userMap.put("userPic",
							domainUrl + usersProfile.getUserPics());
					response.add(userMap);
					System.out.println("response:"+response);
				}
				return ResponseEntity.ok(response);
			}else {
				return ResponseEntity.ok(null);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}
	
	// 取得追蹤中資料
		@GetMapping("/secure/users/followeeList")
		public ResponseEntity<?> getFolloweeList(@RequestHeader("Authorization") String authHeader) {
			String token = authHeader.substring(7); // 移除 "Bearer " 前綴
			String userData = jsonWebTokenUtility.validateToken(token);

			if (userData == null) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
			}

			try {
				JSONObject userObj = new JSONObject(userData);
				Integer userId = userObj.getInt("userId");

				List<UsersProfile> followeeList = sfService.getFolloweeList(userId);
				List<Map<String, Object>> response = new ArrayList<>();
				if (followeeList!=null) {
					for (UsersProfile usersProfile : followeeList) {
						Map<String, Object> userMap = new HashMap<>();
						userMap.put("oppositeId", usersProfile.getUserId());
						userMap.put("userName", usersProfile.getUserName());
						userMap.put("userPic",
								domainUrl + usersProfile.getUserPics());
						response.add(userMap);
						System.out.println("response:"+response);
					}
					return ResponseEntity.ok(response);
				}else {
					return ResponseEntity.ok(null);
				}
			} catch (Exception e) {
				return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
						.body("Error processing user data");
			}
		}
		
		// 取得粉絲資料
				@GetMapping("/secure/users/followerList")
				public ResponseEntity<?> getFollowerList(@RequestHeader("Authorization") String authHeader) {
					String token = authHeader.substring(7); // 移除 "Bearer " 前綴
					String userData = jsonWebTokenUtility.validateToken(token);

					if (userData == null) {
						return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
					}

					try {
						JSONObject userObj = new JSONObject(userData);
						Integer userId = userObj.getInt("userId");

						List<UsersProfile> followerList = sfService.getFollowerList(userId);
						List<Map<String, Object>> response = new ArrayList<>();
						if (followerList!=null) {
							for (UsersProfile usersProfile : followerList) {
								Map<String, Object> userMap = new HashMap<>();
								userMap.put("oppositeId", usersProfile.getUserId());
								userMap.put("userName", usersProfile.getUserName());
								userMap.put("userPic",
										domainUrl + usersProfile.getUserPics());
								response.add(userMap);
								System.out.println("response:"+response);
							}
							return ResponseEntity.ok(response);
						}else {
							return ResponseEntity.ok(null);
						}
					} catch (Exception e) {
						return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
								.body("Error processing user data");
					}
				}
}
