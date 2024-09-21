package com.meetu.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.Activities;
import com.meetu.model.Attendees;
import com.meetu.model.Notification;
import com.meetu.model.NotificationDTO;
import com.meetu.model.Users;
import com.meetu.service.ActivitiesService;
import com.meetu.service.AttendeesService;
import com.meetu.service.NotificationService;
import com.meetu.service.UserService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;

///// 一般寫法 /////
//@Controller
//public class NotificationController {
//
//	@Autowired
//	private NotificationService notiService;
//	
//	@Autowired
//	private UserService userService;
//	
//	@GetMapping("/notification/add")
//	public String addNoti(Model model) {
//		
//		List<Users> usersList = userService.getAllUsers();
//        model.addAttribute("usersList", usersList);
//        return "notification/addNotiPage";
//	}
//	
//	@PostMapping("/notification/addPost")
//	public String addNotiPost(@RequestParam Users notificationUserId, @RequestParam String notificationTitle, @RequestParam String notificationContent) {
//		
//		Notification notification = new Notification();
//		
//		notification.setUsers(notificationUserId);
//		notification.setNotificationTitle(notificationTitle);
//		notification.setNotificationContent(notificationContent);
//		
//		notiService.saveNotification(notification);
//		
//		return "notification/addNotiPage";
//		
//	}
//	
//	@GetMapping("/notification/list")
//	public String FindAll(Model model) {
//		List<Notification> list = notiService.findAllNoti();
//		
//		model.addAttribute("notiList", list);
//		
//		return "notification/showNotiPage";
//	}
//	
//	
//	@GetMapping("/notification/delete")
//	public String deleteNoti(@RequestParam Integer id) {
//		notiService.deleteNotiById(id);
//		
//		return "redirect:/notification/list";
//		
//	}
//	
//	@GetMapping("/notification/update")
//	public String editNoti(@RequestParam Integer id, Model model) {
//		Notification notiById = notiService.fingNotiById(id);
//		model.addAttribute("notiById", notiById);
//		
//		List<Users> usersList = userService.getAllUsers();
//        model.addAttribute("usersList", usersList);
//		
//		return "notification/editNotiPage";
//		
//	}
//	
//	@PostMapping("/notification/updatePost")
//	public String editNotiPost(@ModelAttribute Notification noti) {
//		
//		notiService.saveNotification(noti);
//		
//		return "redirect:/notification/list";
//	
//	}
//	
//}

///// Restful /////
@RestController
@CrossOrigin
public class NotificationController {

	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private ActivitiesService actService;
	
	@Autowired
	private AttendeesService atenService;
	
	@Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;
	
	@Value("${domain.url}")
	private String domainUrl;	
	
	@PostMapping("/notification/add")
	public ResponseEntity<Notification> addNoti(@RequestParam Users notificationUserId, @RequestParam String notificationTitle, @RequestParam String notificationContent) {
		
		Notification notification = new Notification(notificationTitle, notificationContent, notificationUserId);
//		notification.setIsGlobal('0');
		Notification saveNotification = notiService.saveNotification(notification);
		
//		return "notification/addNotiPage";
//		return "add OK!";
		return ResponseEntity.ok(saveNotification);
				
	}
	
	@PostMapping("/notification/addAll")
	public ResponseEntity<?> addNotiToAll(@RequestParam String notificationTitle, @RequestParam String notificationContent) {
		
		List<Users> allUsers = userService.showAllUsers();
		List<Notification> savedNotifications = new ArrayList<>();
		
		for(Users user : allUsers) {
			Notification notification = new Notification(notificationTitle, notificationContent, user);
//			notiService.saveNotification(notification);
			notification.setIsGlobal('1');  // 設置為全體通知
			
			// 移除 userPics 中的 domainUrl
            String userPics = user.getUsersProfile().getUserPics();
            if (userPics != null && userPics.startsWith(domainUrl)) {
                userPics = userPics.substring(domainUrl.length());
                user.getUsersProfile().setUserPics(userPics);
            }
            savedNotifications.add(notiService.saveNotification(notification));
		}
		
		return ResponseEntity.ok("Send to all users");
	}
	
	
	@GetMapping("/notification/list")
	public ResponseEntity<List<Notification>> findAll() {
		
		List<Notification> list = notiService.findAllNoti();
		
		if(list != null) {
//			return "notification/showNotiPage";
//			return list;
			return ResponseEntity.ok(list);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	
	@DeleteMapping("/notification/delete/{nid}")
	public ResponseEntity<Void> deleteNoti(@PathVariable Integer nid) {
		
		Notification fingNotiById = notiService.fingNotiById(nid);
		
		if(fingNotiById != null) {
			
			notiService.deleteNotiById(nid);
			
//			return "redirect:/notification/list";
//			return "Delete OK!";
			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.notFound().build();
	}
	
	
	
	@GetMapping("/notification/findNotiById/{nid}")
	public ResponseEntity<NotificationDTO> fingNotiById(@PathVariable Integer nid) {
		
		Notification notiById = notiService.fingNotiById(nid);
		
        if(notiById != null) {
        	NotificationDTO dto = new NotificationDTO();
            dto.setNotificationId(notiById.getNotificationId());
            dto.setNotificationUserId(notiById.getUsers().getUserId());
            dto.setNotificationTitle(notiById.getNotificationTitle());
            dto.setNotificationContent(notiById.getNotificationContent());
            dto.setNotificationTime(notiById.getNotificationTime());
            return ResponseEntity.ok(dto);
        }
        
//		return "notification/editNotiPage";
//		return notiById;
		return ResponseEntity.notFound().build();
        
	}
	
	@PutMapping("/notification/update/{nid}")
	public ResponseEntity<NotificationDTO> editNoti(
			@PathVariable Integer nid,
			@RequestBody NotificationDTO notificationDTO) {
		
		Notification updateNoti = notiService.fingNotiById(nid);
		
		if(updateNoti == null) {
			return ResponseEntity.notFound().build();
		}
		
		Users user = userService.showUser(notificationDTO.getNotificationUserId());
		
		if(user == null) {
			return ResponseEntity.badRequest().build();
		}
		
		updateNoti.setNotificationTitle(notificationDTO.getNotificationTitle());
        updateNoti.setNotificationContent(notificationDTO.getNotificationContent());
        
     // 移除 userPics 中的 domainUrl
        String userPics = user.getUsersProfile().getUserPics();
        if (userPics != null && userPics.startsWith(domainUrl)) {
            userPics = userPics.substring(domainUrl.length());
            user.getUsersProfile().setUserPics(userPics);
        }
        updateNoti.setUsers(user);
		
        notiService.saveNotification(updateNoti);
        
//		return "redirect:/notification/list";
//		return "update OK!";
        NotificationDTO responseDTO = new NotificationDTO();
        responseDTO.setNotificationId(updateNoti.getNotificationId());
        responseDTO.setNotificationUserId(updateNoti.getUsers().getUserId());
        responseDTO.setNotificationTitle(updateNoti.getNotificationTitle());
        responseDTO.setNotificationContent(updateNoti.getNotificationContent());
        responseDTO.setIsGlobal(updateNoti.getIsGlobal());
        
        return ResponseEntity.ok(responseDTO);
		
	}
	
	@GetMapping("/notification/users")
	public ResponseEntity<List<Users>> getAllUsers() {
	    List<Users> usersList = userService.showAllUsers();
	    if (usersList != null && !usersList.isEmpty()) {
	        return ResponseEntity.ok(usersList);
	    }
	    return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/secure/notification/list/{userId}")
    public ResponseEntity<?> findAllByUserId(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
		// 驗證 token
        String token = authHeader.substring(7); // 移除 "Bearer " 前缀
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }
        
        try {
            JSONObject userObj = new JSONObject(userData);
            Integer loginId = userObj.getInt("userId");
            

            if (loginId != userId) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You are not authorized to access this resource");
            }
            
            List<Notification> list = notiService.findNotiByUserId(userId);
            
            if(list != null) {
                return ResponseEntity.ok(list);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing user data");
        }
    }
	
	
	@GetMapping("/secure/notification/latest/{userId}")
    public ResponseEntity<?> findLatestByUserId(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
        // 驗證 token
        String token = authHeader.substring(7); // 移除 "Bearer " 前綴
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("無效的令牌");
        }
        
        try {
            JSONObject userObj = new JSONObject(userData);
            Integer loginId = userObj.getInt("userId");
            
            if (loginId != userId) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("您沒有權限訪問此資源");
            }
            
            List<Notification> list = notiService.findLatestNotiByUserId(userId);
            
            if(list != null && !list.isEmpty()) {
                return ResponseEntity.ok(list);
            }
            
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("處理用戶數據時出錯");
        }
    }
	
	
	@PutMapping("/secure/notification/markAsRead/{nid}")
    public ResponseEntity<?> markNotiAsRead(@PathVariable Integer nid, @RequestHeader("Authorization") String authHeader) {
        // 驗證 token
        String token = authHeader.substring(7); // 移除 "Bearer " 前綴
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("無效的令牌");
        }
        
        try {
            JSONObject userObj = new JSONObject(userData);
            Integer loginId = userObj.getInt("userId");
            
            Notification notification = notiService.fingNotiById(nid);
            if(notification == null) {
            	return ResponseEntity.notFound().build();
            }
            
            if (!notification.getUsers().getUserId().equals(loginId)) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("您沒有權限訪問此資源");
            }
            
            notification.setNotificationRead('1');
            notiService.markNoti(nid);
            
            return ResponseEntity.ok().body("已變更為已讀狀態");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("處理用戶數據時出錯");
        }
    }
	
//// 使用者報名活動 通知舉辦人
	@PostMapping("/notifications/signup")
    public ResponseEntity<?> createSignupNotification(@RequestBody Map<String, Integer> payload, @RequestHeader("Authorization") String authHeader) {
        Integer activityId = payload.get("activitiesId");
        Integer userId = payload.get("userId");

        // 驗證 token
        String token = authHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("無效的令牌");
        }

        try {
            Activities activity = actService.findActById(activityId);
            if (activity == null) {
                return ResponseEntity.badRequest().body("找不到指定的活動");
            }

            Users signupUser = userService.showUser(userId);
            if (signupUser == null) {
                return ResponseEntity.badRequest().body("找不到指定的用戶");
            }
            
         // 移除 userPics 中的 domainUrl
            String userPics = signupUser.getUsersProfile().getUserPics();
            if (userPics != null && userPics.startsWith(domainUrl)) {
                userPics = userPics.substring(domainUrl.length());
                signupUser.getUsersProfile().setUserPics(userPics);
            }

            Users host = activity.getUsers();
            String notificationTitle = "~有人報名你的活動囉~";
            String notificationContent = signupUser.getUsersProfile().getUserName() + " 已報名了你的活動：" + activity.getActivitiesTitle() + "，記得審核哦！";

            Notification notification = new Notification();
            notification.setNotificationTitle(notificationTitle);
            notification.setNotificationContent(notificationContent);
            notification.setUsers(host);

            notiService.saveNotification(notification);

            return ResponseEntity.ok("通知已發送給活動舉辦人");
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("創建通知時發生錯誤：" + e.getMessage());
        }
    }
	
////舉辦人審核通過 通知申請人
	@PostMapping("/notifications/attendeesApprove")
	public ResponseEntity<?> approveAndNotifyAttendee(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authHeader) {
	    try {
	        // 驗證 JWT token
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

	        // 活動是否存在
	        Activities activity = actService.findActById(activityId);
	        if (activity == null) {
	            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Activity not found");
	        }

	        // 目前user是否是活動的主辦人
	        JSONObject userObj = new JSONObject(userData);
	        Integer currentUserId = userObj.getInt("userId");
	        if (!activity.getUsers().getUserId().equals(currentUserId)) {
	            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("You are not authorized to approve attendees for this activity");
	        }

	        // 審核參加者
	        Attendees attendee = atenService.findAttendeeByActivityAndUserId(activity, attendeeId);
	        if (attendee == null) {
	            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Attendee not found");
	        }

	        // 執行審核邏輯
	        attendee.setApproved(true);
	        atenService.saveAttendee(attendee);

	        // 發送通知給申請人
	        String notificationTitle = "~活動審核通知~";
	        String notificationContent = "你申請報名 " + activity.getActivitiesTitle() + " 的活動，已經通過審核囉～記得準時出席唷！";
	        
	        Notification notification = new Notification();
	        notification.setNotificationTitle(notificationTitle);
	        notification.setNotificationContent(notificationContent);
	        notification.setUsers(attendee.getUsers());
	        notiService.saveNotification(notification);

	        return ResponseEntity.ok("Attendee approved and notified successfully");

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
	                .body("An error occurred: " + e.getMessage());
	    }
	}
	
	private String getActivityLink(Integer activityId) {
	    // 返回前端活動頁面
	    return "localhost:8080/meetu//activities/" + activityId;
	}

	
}
