package com.meetu.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.UsersProfile;
import com.meetu.model.chatroom.Chatroom;
import com.meetu.model.chatroom.ChatroomDetail;
import com.meetu.model.matching.Matching;
import com.meetu.model.matching.MatchingId;
import com.meetu.service.ChatroomService;
import com.meetu.service.ManagersManageService;
import com.meetu.service.MatchingService;
import com.meetu.service.UsersBanService;
import com.meetu.service.UsersProfileService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class MatchingController {

	@Autowired
	public MatchingService mService;

	@Autowired
	public UsersProfileService upService;

	@Autowired
	public ChatroomService cService;

	@Autowired
	public ManagersManageService mmService;
	
	@Autowired
	public UsersBanService uBanService;
	
	@Value("${domain.url}")
	private String domainUrl;

	@Autowired
	public JsonWebTokenUtility jsonWebTokenUtility;

	@GetMapping("/secure/users/matchingcards")
	public ResponseEntity<?> getMatchingCard(@RequestHeader("Authorization") String authHeader) {
		try {
			// 驗證 token
			String token = authHeader.substring(7); // 移除 "Bearer " 前綴
			String userData = jsonWebTokenUtility.validateToken(token);

			if (userData == null) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
			}

			// 解析用戶數據
			JSONObject userJson = new JSONObject(userData);
			Integer loginId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID

			//需先確定每日配對上限是否已設定
			Integer matchingLimit = mmService.getMatchingQty(loginId); // 查詢每天最多可以配對數量

			// 先搜尋當日已配對數量，再撈出可配對數量，並且今日已配對過的人不會再出現
			Integer todayMatchingQty = mService.countTodayMatchingQty(loginId);
			JSONObject response = new JSONObject();
			if (todayMatchingQty >= matchingLimit) {
				response.put("ifHaventFinish", false);
				response.put("matchingCards", "");
				return ResponseEntity.ok(response.toString());
			} else {
				List<UsersProfile> matchingCards = upService.getMatchingCard(loginId);
				if (matchingCards != null) {
					JSONArray usersProfileArray = new JSONArray();
					System.out.println("matchingCards:"+matchingCards);

					for (UsersProfile matchingCard : matchingCards) {
						
						Matching userMatching = mService.getUserMatching(loginId,matchingCard.getUserId());
						boolean ifBan = uBanService.checkBan(loginId, matchingCard.getUserId());
						boolean ifBannedMe = uBanService.checkBan(matchingCard.getUserId(), loginId);
						if(userMatching!=null && userMatching.getChatroomId()!=null) {
							//如果有配對成功紀錄則不放入配對列表
						} 
						
						else if (ifBan==true || ifBannedMe==true) {
							//如果封鎖對方或被對方封鎖則不放入配對列表
						}						
						
						//如果今天沒配對過
						else if(userMatching == null 
							    || userMatching.getMatchedDate() == null 
							    || !userMatching.getMatchedDate().equals(LocalDate.now())) {
							JSONObject usersProfile = new JSONObject();
							usersProfile.put("userId", matchingCard.getUserId());
							usersProfile.put("userName", matchingCard.getUserName());
							usersProfile.put("userGender", String.valueOf(matchingCard.getUserGender()));
							usersProfile.put("userPics", domainUrl+matchingCard.getUserPics());
							usersProfile.put("userBirth", matchingCard.getUserBirth());
							usersProfile.put("userLocation", matchingCard.getUserLocation());
							usersProfile.put("userJob", matchingCard.getUserJob());
							usersProfile.put("userJobPosi", matchingCard.getUserJobPosi());
							usersProfile.put("userIntroduction", matchingCard.getUserIntroduction());
							usersProfile.put("userPreferAct", String.valueOf(matchingCard.getUserPreferAct()));
							usersProfile.put("userPreferGen", String.valueOf(matchingCard.getUserPreferGen()));
							usersProfile.put("userFind", String.valueOf(matchingCard.getUserFind()));
							usersProfile.put("userHobby", matchingCard.getUserHobby());
							
							usersProfileArray.put(usersProfile);		
							
							//超過今日配對上限，則無法再配對
							if(usersProfileArray.length()>=(matchingLimit-todayMatchingQty)) {
								break;
							}
						} 	
						//今天配對過不執行任何動作，直接略過這個配對對象							
					}
					
					//假如使用者可配對條件數量低於今日可配對數量，且都已配對完成
					if(usersProfileArray.length() == 0) {
						response.put("ifHaventFinish", false);
						response.put("matchingCards", "");
					} else {
						response.put("ifHaventFinish", true);
						response.put("matchingCards", usersProfileArray);						
					}
					
				} else {
					response.put("ifHaventFinish", false);
					response.put("matchingCards", "");
				}
			}
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Error processing user data");
		}
	}

	@PostMapping("/secure/users/matchingcards")
	public ResponseEntity<?> likeOrDislike(@RequestHeader("Authorization") String authHeader,
			@RequestBody String json) {
		try {
			// 驗證 token
			String token = authHeader.substring(7); // 移除 "Bearer " 前綴
			String userData = jsonWebTokenUtility.validateToken(token);

			if (userData == null) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
			}

			// 解析用戶數據
			Integer loginId = new JSONObject(userData).getInt("userId");
			JSONObject matchingDetail = new JSONObject(json);
			System.out.println("json:"+json);
			Integer userPreferId = matchingDetail.getInt("userPreferId");			
			boolean likeOrNot = matchingDetail.getBoolean("likeOrNot");
			String matchedDateStr = matchingDetail.optString("matchedDate");
			System.out.println("matchedDateStr:"+matchedDateStr);
			LocalDate matchedDate = matchedDateStr!=""?LocalDate.parse(matchedDateStr):null;
			System.out.println("matchedDate:"+matchedDate);
			MatchingId matchingId = new MatchingId(loginId,userPreferId);
			Matching matching = new Matching();
			matching.setId(matchingId);
			matching.setLikeOrNot(likeOrNot);
			matching.setMatchedDate(matchedDate);
			Matching savedMatchingResult = mService.saveMatchingResult(matching);
			
			JSONObject response = new JSONObject();
			if (savedMatchingResult!=null) {
				response.put("MatchedDate", savedMatchingResult.getMatchedDate());				
				// 回傳配對成功的日期，當作配對成功的訊息。若為null則表示尚未配對成功
				response.put("MatchedSuccessfullyDate", savedMatchingResult.getMatchedSuccessfullyDate());				
				response.put("userPersonalChatroomId", savedMatchingResult.getChatroomId());
				System.out.println(response);
				return ResponseEntity.ok(response.toString());
			} else {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("儲存配對出事了");
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok("出事啦!!!");
		}
	}
}
