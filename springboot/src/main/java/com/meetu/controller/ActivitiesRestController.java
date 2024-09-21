package com.meetu.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.meetu.model.Activities;
import com.meetu.model.ActivitiesComment;
import com.meetu.model.ActivitiesDTO;
import com.meetu.model.Users;
import com.meetu.model.chatroom.ChatroomAct;
import com.meetu.service.ActivitiesCommentService;
import com.meetu.service.ActivitiesService;
import com.meetu.service.ChatroomService;
import com.meetu.service.UserService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
public class ActivitiesRestController {

	@Autowired
	private ActivitiesService actService;

	@Autowired
	private ActivitiesCommentService commentService;

	@Autowired
	public JsonWebTokenUtility jsonWebTokenUtility;

	@Autowired
	private UserService uService;
	
	@Autowired
	private ChatroomService cService;

	@PostMapping("/activities")
	public ResponseEntity<?> addActivity(@RequestParam String activitiesType,
			@RequestParam MultipartFile activitiesPics, @RequestParam String activitiesTitle,
			@RequestParam LocalDateTime activitiesStartDate, @RequestParam LocalDateTime activitiesEndDate,
			@RequestParam String activitiesLocation, @RequestParam LocalDateTime activitiesVerifyDate,
			@RequestParam String activitiesContent, @RequestParam String activitiesSharing,
			@RequestParam Integer activitiesAmt, @RequestParam Integer activitiesMaxPeo,
			@RequestHeader("Authorization") String authHeader) throws IOException {

//		 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		JSONObject userJson = new JSONObject(userData);
		Integer userId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID

		Users users = new Users();
		users.setUserId(userId);

		Activities newAct = new Activities();
		newAct.setActivitiesReportStatus("N");
		newAct.setActivitiesType(activitiesType);
		newAct.setUsers(users);
		newAct.setActivitiesPics(activitiesPics.getBytes());
		newAct.setActivitiesTitle(activitiesTitle);
		newAct.setActivitiesStartDate(activitiesStartDate);
		newAct.setActivitiesEndDate(activitiesEndDate);
		newAct.setActivitiesLocation(activitiesLocation);
		newAct.setActivitiesVerifyDate(activitiesVerifyDate);
		newAct.setActivitiesContent(activitiesContent);
		newAct.setActivitiesSharing(activitiesSharing);
		newAct.setActivitiesAmt(activitiesAmt);
		newAct.setActivitiesMaxPeo(activitiesMaxPeo);

		Activities saveActivities = actService.saveActivities(newAct);
		
		//建立活動後同時建立活動聊天室
		ChatroomAct actChatroom = cService.createActChatroom(saveActivities);
		System.out.println(actChatroom);

		return ResponseEntity.ok(saveActivities);

	}
	//Service跟repository已過濾條件(日期、N)
	 @GetMapping("/activities")
	    public List<Activities> getAllActivities() {
	        return actService.findValidActivities();
	    }

	
	@GetMapping("/activities/{aid}")
	public ResponseEntity<Map<String, Object>> findById(@PathVariable("aid") Integer aid) {
	    Activities actById = actService.findActById(aid);
	    if (actById == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Collections.singletonMap("message", "Activity not found"));
	    }

	    // 取得活動留言
	    List<ActivitiesComment> comments = commentService.findCommentsByActivityId(aid);
	    
	    
	    List<Map<String, Object>> commentsWithUserInfo = new ArrayList<>();
	    
	    for (ActivitiesComment comment : comments) {
	        Map<String, Object> commentMap = new HashMap<>();
	        commentMap.put("activitiesCommentId", comment.getActivitiesCommentId());
	        commentMap.put("messageContent", comment.getMessageContent());
	        commentMap.put("messageTime", comment.getMessageTime());
	        commentMap.put("userId", comment.getUserId());
	        commentMap.put("userName", comment.getUserName());
	        commentMap.put("userPics", comment.getUserPics());
	        commentsWithUserInfo.add(commentMap);
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("activity", actById);
	    response.put("comments", commentsWithUserInfo);

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/photos/download/{id}")
	public ResponseEntity<byte[]> downloadPhoto(@PathVariable Integer id) {
		Activities activities = actService.findActById(id);

		if (activities != null) {
			byte[] photoFile = activities.getActivitiesPics();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<>(photoFile, headers, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	
	 @GetMapping("/usersActList")
	    public ResponseEntity<?> getUserActivities(@RequestHeader("Authorization") String authHeader) {
	        String token = authHeader.substring(7); // 移除 "Bearer " 前缀
	        String userData = jsonWebTokenUtility.validateToken(token);

	        if (userData == null) {
	            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	        }

	        JSONObject userJson = new JSONObject(userData);
	        Integer userId = userJson.getInt("userId");

	        List<Activities> upcomingActivities = actService.findUpcomingActivities(userId);
	        List<Activities> pastActivities = actService.findPastActivities(userId);

	        Map<String, Object> response = new HashMap<>();
	        response.put("upcomingActivities", upcomingActivities);
	        response.put("pastActivities", pastActivities);

	        return ResponseEntity.ok(response);
	    }

	@DeleteMapping("/activities/{activitiesId}")
	public String deleteActivity(@PathVariable Integer activitiesId) {
		boolean deleteMsg = actService.deleteActById(activitiesId);

		if (deleteMsg) {

			return "delete ok";

		}
		return "delete not ok";
	}

	@PutMapping("/activities/{activitiesId}")
	public ResponseEntity<String> updateActivity(@PathVariable Integer activitiesId,
			@ModelAttribute ActivitiesDTO activityUpdateDTO, @RequestHeader("Authorization") String authHeader)
			throws IOException {

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + authHeader);
//		 驗證 token
		String token = authHeader.substring(7); // 移除 "Bearer " 前綴
		String userData = jsonWebTokenUtility.validateToken(token);

		if (userData == null) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
		}

		JSONObject userJson = new JSONObject(userData);
		Integer userId = userJson.getInt("userId"); // 假設 token 中包含用戶 ID

		Users users = new Users();
		users.setUserId(userId);

		Activities activities = actService.findActById(activitiesId);

		activities.setActivitiesType(activityUpdateDTO.getActivitiesType());
		activities.setActivitiesTitle(activityUpdateDTO.getActivitiesTitle());
		activities.setActivitiesStartDate(activityUpdateDTO.getActivitiesStartDate());
		activities.setActivitiesEndDate(activityUpdateDTO.getActivitiesEndDate());
		activities.setActivitiesLocation(activityUpdateDTO.getActivitiesLocation());
		activities.setActivitiesVerifyDate(activityUpdateDTO.getActivitiesVerifyDate());
		activities.setActivitiesContent(activityUpdateDTO.getActivitiesContent());
		activities.setActivitiesSharing(activityUpdateDTO.getActivitiesSharing());
		activities.setActivitiesAmt(activityUpdateDTO.getActivitiesAmt());
		activities.setActivitiesMaxPeo(activityUpdateDTO.getActivitiesMaxPeo());
		activities.setUsers(users);

		if (activityUpdateDTO.getActivitiesPics() != null && !activityUpdateDTO.getActivitiesPics().isEmpty()) {
			activities.setActivitiesPics(activityUpdateDTO.getActivitiesPics().getBytes());
		}

		try {
			actService.saveActivities(activities);
			return ResponseEntity.ok("Update successful");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Failed to update activity");
		}

	}
	@PutMapping("/activities/{activitiesId}/delete")
	public ResponseEntity<String> markActivityAsDeleted(@PathVariable Integer activitiesId, @RequestHeader("Authorization") String authHeader) {
	    // 驗證 token
	    String token = authHeader.substring(7);
	    String userData = jsonWebTokenUtility.validateToken(token);

	    if (userData == null) {
	        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	    }

	    JSONObject userJson = new JSONObject(userData);
	    Integer userId = userJson.getInt("userId");

	    Activities activity = actService.findActById(activitiesId);
	    if (activity == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found");
	    }

	    // 檢查當前用戶是否是活動的創建者
	    if (!activity.getUsers().getUserId().equals(userId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this activity");
	    }

	    activity.setActivitiesReportStatus("D");
	    actService.saveActivities(activity);

	    return ResponseEntity.ok("Activity marked as deleted");
	}
	
	@GetMapping("/activities/search")
	public ResponseEntity<List<Activities>> searchActivities(@RequestParam String query) {
	    List<Activities> searchResults = actService.searchActivities(query);
	    return ResponseEntity.ok(searchResults);
	}
}
