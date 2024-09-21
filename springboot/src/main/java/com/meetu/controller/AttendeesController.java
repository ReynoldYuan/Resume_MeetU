package com.meetu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
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

import com.meetu.model.Activities;
import com.meetu.model.Attendees;
import com.meetu.model.Notification;
import com.meetu.model.Users;
import com.meetu.model.chatroom.ChatroomDetail;
import com.meetu.model.chatroom.ChatroomDetailId;
import com.meetu.service.ActivitiesService;
import com.meetu.service.AttendeesService;
import com.meetu.service.ChatroomService;
import com.meetu.service.NotificationService;
import com.meetu.service.UserService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin
public class AttendeesController {
	
	
	@Autowired
	private AttendeesService atenService;
	
	@Autowired
	private NotificationService notiService;

	@Autowired
	public JsonWebTokenUtility jsonWebTokenUtility;
	
	@Autowired
	public ActivitiesService aService;
	
	@Autowired
	public ChatroomService cService;
	
	@Autowired
	private UserService uService;
	
	@Value("${domain.url}")
	 private String domainUrl;
	
	
	@PostMapping("/attendees")
	public ResponseEntity<?> addAttendee(@RequestBody Map<String, Object> requestBody, @RequestHeader("Authorization") String authHeader) {
	    Integer activitiesId = (Integer) requestBody.get("activitiesId");
	    String token = authHeader.substring(7);
	    String userData = jsonWebTokenUtility.validateToken(token);

	    if (userData == null) {
	        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	    }

	    JSONObject userJson = new JSONObject(userData);
	    Integer userId = userJson.getInt("userId");

	    Activities activities = aService.findActById(activitiesId);
	    Users users = uService.showUser(userId);

	    System.out.println("控制器開始 - 用戶ID: " + userId);
	    System.out.println("控制器開始 - 用戶圖片路徑: " + users.getUsersProfile().getUserPics());

	    if (activities.getUsers().getUserId().equals(userId)) {
	        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You cannot sign up for your own activity");
	    }

	    boolean alreadyRegistered = atenService.existsByActivitiesAndUsers(activities, users);
	    if (alreadyRegistered) {
	        return ResponseEntity.status(HttpServletResponse.SC_CONFLICT).body("User has already signed up for this activity");
	    }
	    
	 // 移除 userPics 中的 domainUrl
        String userPics = users.getUsersProfile().getUserPics();
        if (userPics != null && userPics.startsWith(domainUrl)) {
            userPics = userPics.substring(domainUrl.length());
            users.getUsersProfile().setUserPics(userPics);
            System.out.println("fuckXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx"+users.getUsersProfile().getUserPics());
        }

	    Attendees attendees = new Attendees();
	    attendees.setActivities(activities);
	    attendees.setUsers(users);
	    

	    Attendees saveAttendee = atenService.saveAttendee(attendees);
	    if (saveAttendee!=null) {
	    	
	    }

	    System.out.println("控制器結束 - 用戶ID: " + userId);
	    System.out.println("控制器結束 - 用戶圖片路徑: " + saveAttendee.getUsers().getUsersProfile().getUserPics());

	    return ResponseEntity.ok(saveAttendee);
	}



	@GetMapping("/attendees")
	public List<Attendees> findAllAttendee() {
		List<Attendees> allAttendees = atenService.findAllAttendees();
		return allAttendees;
	}

	@GetMapping("/attendees/check/{activitiesId}")
	public ResponseEntity<?> checkIfUserSignedUp(@PathVariable("activitiesId") Integer activitiesId, @RequestHeader("Authorization") String authHeader) {
	    String token = authHeader.substring(7);
	    String userData = jsonWebTokenUtility.validateToken(token);

	    if (userData == null) {
	        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	    }

	    JSONObject userJson = new JSONObject(userData);
	    Integer userId = userJson.getInt("userId");

	    Activities activities = aService.findActById(activitiesId);
	    Users users = new Users();
	    users.setUserId(userId);

	    Attendees attendee = atenService.findByActivitiesAndUsers(activities, users);
	    Map<String, Object> response = new HashMap<>();
	    
	    if (attendee != null) {
	        response.put("isSignedUp", true);
	        response.put("isApproved", attendee.isApproved());
	        response.put("entryId", attendee.getEntryId());
	    } else {
	        response.put("isSignedUp", false);
	        response.put("isApproved", false);
	        response.put("entryId", null);
	    }
	    
	    return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/attendees/registrations")
    public ResponseEntity<?> getMyRegistrationsWithStatus(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);

            if (userData == null) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
            }

            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");

            Users user = new Users();
            user.setUserId(userId);
            List<Attendees> myRegistrations = atenService.findByUserWithApprovalStatus(user);

            List<Map<String, Object>> registrationDetails = new ArrayList<>();

            for (Attendees attendee : myRegistrations) {
                Map<String, Object> details = new HashMap<>();
                details.put("entryId", attendee.getEntryId());
                details.put("activityId", attendee.getActivities().getActivitiesId());
                details.put("activityName", attendee.getActivities().getActivitiesTitle());
                details.put("registrationDate", attendee.getRegisteredAt());
                details.put("isApproved", attendee.isApproved());
                details.put("activitiesStartDate", attendee.getActivities().getActivitiesStartDate());
                details.put("activitiesEndDate", attendee.getActivities().getActivitiesEndDate());
                details.put("activitiesReportStatus", attendee.getActivities().getActivitiesReportStatus());
                details.put("activitiesLocation", attendee.getActivities().getActivitiesLocation());
                
                registrationDetails.add(details);
            }

            return ResponseEntity.ok(registrationDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }
	 
	 @GetMapping("/attendees/host-activities")
	 public ResponseEntity<?> getHostActivitiesWithAttendees(@RequestHeader("Authorization") String authHeader) {
	     try {
	         // 提取並驗證 JWT token
	         String token = authHeader.substring(7);
	         String userData = jsonWebTokenUtility.validateToken(token);

	         if (userData == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	         }

	         // 從 JWT 獲取用戶 ID
	         JSONObject userJson = new JSONObject(userData);
	         Integer userId = userJson.getInt("userId");

	         // 查找該用戶舉辦的所有活動
	         List<Activities> hostActivities = aService.findActByUserId(userId);

	         if (hostActivities.isEmpty()) {
	             return ResponseEntity.ok().body("User has not hosted any activities.");
	         }

	         // 創建一個列表來保存響應數據
	         List<Map<String, Object>> activitiesWithAttendees = new ArrayList<>();

	         for (Activities activity : hostActivities) {
	             Map<String, Object> activityDetails = new HashMap<>();
	             activityDetails.put("activityId", activity.getActivitiesId());
	             activityDetails.put("activityName", activity.getActivitiesTitle());
	             activityDetails.put("activitiesStartDate", activity.getActivitiesStartDate());
	             activityDetails.put("activitiesEndDate", activity.getActivitiesEndDate());
	             activityDetails.put("activitiesLocation", activity.getActivitiesLocation());
	             activityDetails.put("activitiesReportStatus", activity.getActivitiesReportStatus());

	             List<Map<String, Object>> allAttendees = new ArrayList<>();

	             for (Attendees attendee : activity.getAttendees()) {
	                 Map<String, Object> attendeeDetails = new HashMap<>();
	                 attendeeDetails.put("attendeeId", attendee.getUsers().getUserId());
	                 attendeeDetails.put("attendeeName", attendee.getUsers().getUsersProfile().getUserName());
	                 attendeeDetails.put("registrationDate", attendee.getRegisteredAt());
	                 attendeeDetails.put("isApproved", attendee.isApproved());
	                 attendeeDetails.put("isCompleted", attendee.isCompleted());
	                 attendeeDetails.put("userPics", attendee.getUsers().getUsersProfile().getUserPics());
	                 allAttendees.add(attendeeDetails);
	             }

	             activityDetails.put("attendees", allAttendees);
	             activitiesWithAttendees.add(activityDetails);
	         }

	         return ResponseEntity.ok(activitiesWithAttendees);
	     } catch (JSONException e) {
	         return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
	                              .body("Invalid token format: " + e.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }
	 
	 @PostMapping("/attendees/approve")
	 public ResponseEntity<?> approveAttendee(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authHeader) {
	     try {
	         // 驗證 JWT token
	         String token = authHeader.substring(7);
	         String userData = jsonWebTokenUtility.validateToken(token);

	         if (userData == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	         }

	         // 從請求體中獲取活動ID和參加者ID
	         Integer activityId = requestBody.get("activityId");
	         Integer attendeeId = requestBody.get("attendeeId");

	         if (activityId == null || attendeeId == null) {
	             return ResponseEntity.badRequest().body("Both activityId and attendeeId are required");
	         }

	         // 驗證活動存在
	         Activities activity = aService.findActById(activityId);
	         if (activity == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Activity not found");
	         }

	         // 驗證當前用戶是否是活動的主辦人
	         JSONObject userJson = new JSONObject(userData);
	         Integer currentUserId = userJson.getInt("userId");
	         if (!activity.getUsers().getUserId().equals(currentUserId)) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You are not authorized to approve attendees for this activity");
	         }

	         // 查找並審核參加者
	         Attendees attendee = atenService.findAttendeeByActivityAndUserId(activity, attendeeId);
	         if (attendee == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Attendee not found");
	         }

	         if (attendee.isApproved()) {
	             return ResponseEntity.ok("Attendee is already approved");
	         }

	         // 執行審核邏輯
	         attendee.setApproved(true);
	         Attendees saveAttendee = atenService.saveAttendee(attendee);
	         
	        Integer chatroomId = null;
	         //確認有儲存審核結果成功才加入聊天室
//	         if(saveAttendee!=null) {
//	        	 ChatroomDetail intoChatroom = cService.addIntoChatroom(activityId, attendeeId);
//	        	 ChatroomDetailId id = intoChatroom.getId();
//	        	 chatroomId = id.getChatroomId();
	        	 
	        if(saveAttendee != null) {
	            try {
	                ChatroomDetail intoChatroom = cService.addIntoChatroom(activityId, attendeeId);
	                if (intoChatroom != null && intoChatroom.getId() != null) {
	                    chatroomId = intoChatroom.getId().getChatroomId();
	                } else {
	                    System.err.println("Failed to add attendee to chatroom or get chatroom ID");
	                }
	            } catch (Exception e) {
	                System.err.println("Error adding attendee to chatroom: " + e.getMessage());
	                // 繼續執行，不要因為聊天室錯誤而中斷整個流程
	            }
	        }
//	         }
	         
	         
	         // 發送通知給申請人
	         String activityLink = "http://localhost:8080/meetu/activities/" + activityId;
		     String notificationTitle = "~活動審核通知~";
		     String notificationContent = "你申請報名 " + activity.getActivitiesTitle() + " 的活動，已經通過審核囉～記得準時出席唷！";
		        
		     Notification notification = new Notification();
		     notification.setNotificationTitle(notificationTitle);
		     notification.setNotificationContent(notificationContent);
		     notification.setUsers(attendee.getUsers());
		     notiService.saveNotification(notification);

	         return ResponseEntity.ok("Attendee approved successfully and add into chatroom "+chatroomId);

	     } catch (JSONException e) {
	         return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
	                              .body("Invalid token format: " + e.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }
	 @DeleteMapping("/attendees/cancel/{entryId}")
	 public ResponseEntity<?> cancelRegistration(@PathVariable Integer entryId, @RequestHeader("Authorization") String authHeader) {
	     try {
	         // 驗證 JWT token
	         String token = authHeader.substring(7);
	         String userData = jsonWebTokenUtility.validateToken(token);

	         if (userData == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	         }

	         // 從 JWT 獲取用戶 ID
	         JSONObject userJson = new JSONObject(userData);
	         Integer userId = userJson.getInt("userId");

	         // 查找並刪除報名記錄
	         boolean canceled = atenService.cancelRegistration(entryId, userId);

	         if (canceled) {
	             return ResponseEntity.ok("Registration canceled successfully");
	         } else {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Registration not found");
	         }

	     } catch (JSONException e) {
	         return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
	                              .body("Invalid token format: " + e.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }
	 
	 @GetMapping("/attendees/activity/{activitiesId}")
	    public ResponseEntity<?> getActivityAttendees(@PathVariable Integer activitiesId) {
	        try {
	            Activities activity = aService.findActById(activitiesId);
	            if (activity == null) {
	                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Activity not found");
	            }

	            List<Attendees> attendees = atenService.findByActivity(activity);
	            int totalAttendees = attendees.size();

	            List<Map<String, Object>> attendeeDetails = new ArrayList<>();
	            for (Attendees attendee : attendees) {
	                Map<String, Object> details = new HashMap<>();
	                details.put("userId", attendee.getUsers().getUserId());
	                details.put("userName", attendee.getUsers().getUsersProfile().getUserName());
	                details.put("userPics", attendee.getUsers().getUsersProfile().getUserPics());
	                details.put("isApproved", attendee.isApproved());
	                attendeeDetails.add(details);
	            }

	            Map<String, Object> response = new HashMap<>();
	            response.put("totalAttendees", totalAttendees);
	            response.put("attendees", attendeeDetails);

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                                 .body("An error occurred: " + e.getMessage());
	        }
	    }
	 
	 @PostMapping("/attendees/confirm-attendance")
	 public ResponseEntity<?> confirmAttendance(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authHeader) {
	     try {
	         // 驗證 JWT token
	         String token = authHeader.substring(7);
	         String userData = jsonWebTokenUtility.validateToken(token);

	         if (userData == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	         }

	         // 從請求體中獲取活動ID和參加者ID
	         Integer activityId = requestBody.get("activityId");
	         Integer attendeeId = requestBody.get("attendeeId");

	         if (activityId == null || attendeeId == null) {
	             return ResponseEntity.badRequest().body("Both activityId and attendeeId are required");
	         }

	         // 驗證活動存在
	         Activities activity = aService.findActById(activityId);
	         if (activity == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Activity not found");
	         }

	         // 驗證當前用戶是否是活動的主辦人
	         JSONObject userJson = new JSONObject(userData);
	         Integer currentUserId = userJson.getInt("userId");
	         if (!activity.getUsers().getUserId().equals(currentUserId)) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You are not authorized to confirm attendance for this activity");
	         }

	         // 查找並確認參加者
	         Attendees attendee = atenService.findAttendeeByActivityAndUserId(activity, attendeeId);
	         if (attendee == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Attendee not found");
	         }

	         if (attendee.isCompleted()) {
	             return ResponseEntity.ok("Attendance already confirmed");
	         }

	         // 執行確認參加邏輯
	         attendee.setCompleted(true);
	         atenService.saveAttendee(attendee);

	         return ResponseEntity.ok("Attendance confirmed successfully");

	     } catch (JSONException e) {
	         return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
	                              .body("Invalid token format: " + e.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }
	 
	 @GetMapping("/attendees/past-activities")
	 public ResponseEntity<?> getPastActivities(@RequestHeader("Authorization") String authHeader) {
	     try {
	    	 // 驗證 JWT token
	            String token = authHeader.substring(7);
	            String userData = jsonWebTokenUtility.validateToken(token);

	            if (userData == null) {
	                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	            }

	            // 從 JWT 獲取用戶 ID
	            JSONObject userJson = new JSONObject(userData);
	            Integer userId = userJson.getInt("userId");

	         // 查找用戶完成的活動
	         List<Map<String, Object>> completedActivities = atenService.findPastActivitiesByUserId(userId);

	         if (completedActivities.isEmpty()) {
	             return ResponseEntity.ok().body("User has not completed any activities.");
	         }

	         return ResponseEntity.ok(completedActivities);

	     } catch (JSONException e) {
	         return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
	                              .body("Invalid token format: " + e.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }
	 
	 @PostMapping("/attendees/cancel-approval")
	 public ResponseEntity<?> cancelApproval(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authHeader) {
	     try {
	         String token = authHeader.substring(7);
	         String userData = jsonWebTokenUtility.validateToken(token);

	         if (userData == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	         }

	         Integer activityId = requestBody.get("activityId");
	         Integer attendeeId = requestBody.get("attendeeId");

	         if (activityId == null || attendeeId == null) {
	             return ResponseEntity.badRequest().body("Both activityId and attendeeId are required");
	         }

	         Activities activity = aService.findActById(activityId);
	         if (activity == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Activity not found");
	         }

	         JSONObject userJson = new JSONObject(userData);
	         Integer currentUserId = userJson.getInt("userId");
	         if (!activity.getUsers().getUserId().equals(currentUserId)) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You are not authorized to cancel approval for this activity");
	         }

	         Attendees attendee = atenService.findAttendeeByActivityAndUserId(activity, attendeeId);
	         if (attendee == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Attendee not found");
	         }

	         if (!attendee.isApproved()) {
	             return ResponseEntity.ok("Attendee is not approved");
	         }

	         attendee.setApproved(false);
	         atenService.saveAttendee(attendee);

	         return ResponseEntity.ok("Approval cancelled successfully");

	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }

	 @PostMapping("/attendees/cancel-confirmation")
	 public ResponseEntity<?> cancelConfirmation(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authHeader) {
	     try {
	         String token = authHeader.substring(7);
	         String userData = jsonWebTokenUtility.validateToken(token);

	         if (userData == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
	         }

	         Integer activityId = requestBody.get("activityId");
	         Integer attendeeId = requestBody.get("attendeeId");

	         if (activityId == null || attendeeId == null) {
	             return ResponseEntity.badRequest().body("Both activityId and attendeeId are required");
	         }

	         Activities activity = aService.findActById(activityId);
	         if (activity == null) {
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Activity not found");
	         }

	         JSONObject userJson = new JSONObject(userData);
	         Integer currentUserId = userJson.getInt("userId");
	         if (!activity.getUsers().getUserId().equals(currentUserId)) {
	             return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You are not authorized to cancel confirmation for this activity");
	         }

	         System.out.println("開始取消確認參加: 活動ID = " + activityId + ", 參加者ID = " + attendeeId);
	         
	         Attendees attendee = atenService.findAttendeeByActivityAndUserId(activity, attendeeId);
	         if (attendee == null) {
	             System.out.println("未找到參加者");
	             return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Attendee not found");
	         }
	         
	         System.out.println("當前參加狀態: " + attendee.isCompleted());

	         if (!attendee.isCompleted()) {
	             System.out.println("參加者尚未確認參加");
	             return ResponseEntity.ok("Attendance is not confirmed");
	         }

	         attendee.setCompleted(false);
	         atenService.saveAttendee(attendee);

	         return ResponseEntity.ok("Confirmation cancelled successfully");

	     } catch (Exception e) {
	         return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                              .body("An error occurred: " + e.getMessage());
	     }
	 }
	 
	 @GetMapping("/test")
	 public ResponseEntity<String> test() {
	     return ResponseEntity.ok("Test successful. Context path is working.");
	 }

}
