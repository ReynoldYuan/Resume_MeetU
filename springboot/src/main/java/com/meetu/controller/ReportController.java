package com.meetu.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.Activities;
import com.meetu.model.ActivitiesComment;
import com.meetu.model.Notification;
import com.meetu.model.Report;
import com.meetu.model.Users;
import com.meetu.service.ActivitiesCommentService;
import com.meetu.service.ActivitiesService;
import com.meetu.service.NotificationService;
import com.meetu.service.ReportService;
import com.meetu.service.UserService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class ReportController {

	@Autowired
	private ReportService reportService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ActivitiesService activitiesService;
	
	@Autowired
	private ActivitiesCommentService actCommentService;
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;
	
	@Value("${domain.url}")
	private String domainUrl;
	
	
	@PostMapping("/report/add")
    public ResponseEntity<?> addReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String json) {

        // 驗證 token
        String token = authHeader.substring(7); // 移除 "Bearer " 前綴
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }

        try {
            JSONObject userObj = new JSONObject(userData);
            Integer reportUserId = userObj.getInt("userId");

            JSONObject jsonObject = new JSONObject(json);
            String reportItem = jsonObject.getString("reportItem");
            Integer reportItemId = jsonObject.getInt("reportItemId");
            String reportType = jsonObject.getString("reportType");
            String reportReason = jsonObject.getString("reportReason");

            Users user = userService.showUser(reportUserId);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
         // 移除 userPics 中的 domainUrl
            String userPics = user.getUsersProfile().getUserPics();
            if (userPics != null && userPics.startsWith(domainUrl)) {
                userPics = userPics.substring(domainUrl.length());
                user.getUsersProfile().setUserPics(userPics);
            }


            Report report = new Report(user, reportItem, reportItemId, reportType, reportReason);
            Report savedReport = reportService.saveReport(report);

            return ResponseEntity.ok(savedReport);

        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing report data: " + e.getMessage());
        }
    }
	
	
	@GetMapping("/report/status/{reportItem}/{reportItemId}")
    public ResponseEntity<?> getReportStatus(
            @PathVariable String reportItem,
            @PathVariable Integer reportItemId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }

        try {
            JSONObject userObj = new JSONObject(userData);
            Integer reportUserId = userObj.getInt("userId");

            boolean ifReport = reportService.existsReport(reportUserId, reportItemId, reportItem);

            JSONObject response = new JSONObject();
            response.put("ifReport", ifReport);

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing report status: " + e.getMessage());
        }
    }
	
	@GetMapping("/report/comment/status/{reportItem}/{reportItemId}")
    public ResponseEntity<?> getCommentReportStatus(
            @PathVariable String reportItem,
            @PathVariable Integer reportItemId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }

        try {
            JSONObject userObj = new JSONObject(userData);
            Integer reportUserId = userObj.getInt("userId");

            boolean ifCommentReport = reportService.existsReport(reportUserId, reportItemId, reportItem);

            JSONObject response = new JSONObject();
            response.put("ifCommentReport", ifCommentReport);

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing report status: " + e.getMessage());
        }
    }
	
	
	@GetMapping("/user/report/list")
    public ResponseEntity<?> getUserReports(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }

        try {
            JSONObject userObj = new JSONObject(userData);
            Integer userId = userObj.getInt("userId");

            List<Report> reports = reportService.findReportByUserId(userId);
            List<JSONObject> reportList = reports.stream().map(report -> {
                JSONObject reportObj = new JSONObject();
                reportObj.put("reportId", report.getReportId());
                reportObj.put("reportItemId", report.getReportItemId());
                reportObj.put("reportItem", report.getReportItem());
                reportObj.put("reportType", report.getReportType());
                reportObj.put("reportReason", report.getReportReason());
                reportObj.put("reportUserId", report.getUsers().getUserId());
                reportObj.put("reportStatus", String.valueOf(report.getReportStatus()));
                
                String reportedContent = "無內容";
                Integer associatedActivityId = null;
                if ("U".equals(report.getReportItem())) {
                    Users reportedUser = userService.showUser(report.getReportItemId());
                    if (reportedUser != null && reportedUser.getUsersProfile() != null) {
                        reportedContent = reportedUser.getUsersProfile().getUserName();
                    }
                } else if ("A".equals(report.getReportItem())) {
                    Activities activity = activitiesService.findActById(report.getReportItemId());
                    if (activity != null) {
                        reportedContent = activity.getActivitiesTitle();
                    }
                } else if ("AC".equals(report.getReportItem())) {
                  ActivitiesComment activitiesComment = actCommentService.findCommentById(report.getReportItemId());
                	
                	if (activitiesComment != null) {
                		reportedContent = activitiesComment.getMessageContent();
                		associatedActivityId = activitiesComment.getActivities().getActivitiesId();
                    }
                }
                reportObj.put("reportedContent", reportedContent);
                if (associatedActivityId != null) {
                    reportObj.put("associatedActivityId", associatedActivityId);
                }
                
                return reportObj;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(reportList.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing report list: " + e.getMessage());
        }
    }

    @DeleteMapping("/user/report/cancel")
    public ResponseEntity<?> cancelReport(@RequestHeader("Authorization") String authHeader,@RequestBody String json) {

        String token = authHeader.substring(7);
        String userData = jsonWebTokenUtility.validateToken(token);
        
        if (userData == null) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
        }

        try {
            JSONObject userObj = new JSONObject(userData);
            Integer userId = userObj.getInt("userId");

            JSONObject jsonObject = new JSONObject(json);
            Integer reportItemId = jsonObject.getInt("reportItemId");
            String reportItem = jsonObject.getString("reportItem");

            reportService.deleteReport(userId, reportItemId, reportItem);

            return ResponseEntity.ok("Delete report");

        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error processing report delete: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/report/list/users")
    public ResponseEntity<?> getUsersReports() {
        List<Report> reports = reportService.findReportsByItem("U");
        List<JSONObject> reportList = reports.stream().map(report -> {
            JSONObject reportObj = new JSONObject();
            reportObj.put("reportId", report.getReportId());
            reportObj.put("reportItemId", report.getReportItemId());
            reportObj.put("reportItem", report.getReportItem());
            reportObj.put("reportType", report.getReportType());
            reportObj.put("reportReason", report.getReportReason());
            reportObj.put("reportUserId", report.getUsers().getUserId());
            reportObj.put("reportStatus", String.valueOf(report.getReportStatus()));
            
            Users reportedUser = userService.showUser(report.getReportItemId());
            if (reportedUser != null && reportedUser.getUsersProfile() != null) {
                reportObj.put("reportedContent", reportedUser.getUsersProfile().getUserName());
            } else {
                reportObj.put("reportedContent", "未找到用戶");
            }
            
            return reportObj;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reportList.toString());
    }
    
    
    @PutMapping("/report/updateStatus")
    public ResponseEntity<?> updateReportStatus(@RequestBody Map<String, Object> payload) {
    	
        try {
            Integer reportId = (Integer) payload.get("reportId");
            String newStatusString = (String) payload.get("newStatus");
            
            if (newStatusString == null || newStatusString.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid new status");
            }
            
            Character newStatus = newStatusString.charAt(0);

            Report updatedReport = reportService.updateReportStatus(reportId, newStatus);
            
            if (updatedReport == null) {
                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Report not found");
            }
            
            if (updatedReport.getReportItem().equals("U")) {
                Users user = userService.showUser(updatedReport.getReportItemId());
                if (user != null) {
                    if (newStatus == 'Y') {
                        user.setUserIsBan('R');
                    } else if (newStatus == 'N') {
                        user.setUserIsBan('N');
                    }
                    
                 // 移除 userPics 中的 domainUrl
                    String userPics = user.getUsersProfile().getUserPics();
                    if (userPics != null && userPics.startsWith(domainUrl)) {
                        userPics = userPics.substring(domainUrl.length());
                        user.getUsersProfile().setUserPics(userPics);
                    }
                    userService.updateUser(user);
                }
            } else if (updatedReport.getReportItem().equals("A")) {
                Activities activity = activitiesService.findActById(updatedReport.getReportItemId());
                if (activity != null) {
                    if (newStatus == 'Y') {
                        activity.setActivitiesReportStatus("R");
                    } else if (newStatus == 'N') {
                        activity.setActivitiesReportStatus("N");
                    }
                    activitiesService.updateActivitiesReportStatus(activity);
                }
            } else if (updatedReport.getReportItem().equals("AC")) { 
                ActivitiesComment comment = actCommentService.findCommentById(updatedReport.getReportItemId());
                if (comment != null) {
                    if (newStatus == 'Y') {
                        comment.setActivitiesReportStatus("R");
                    } else if (newStatus == 'N') {
                        comment.setActivitiesReportStatus("N");
                    }
                    actCommentService.updateActivitiesComment(comment);
                }
            }
            
            if (newStatus == 'Y') {
                sendReportApprovedNotification(updatedReport.getReportItem(), updatedReport.getReportItemId());
            }

            return ResponseEntity.ok("Report status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Error updating report status: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/report/list/activities")
    public ResponseEntity<?> getActivityReports() {
        List<Report> reports = reportService.findReportsByItem("A");
        List<JSONObject> reportList = reports.stream().map(report -> {
            JSONObject reportObj = new JSONObject();
            reportObj.put("reportId", report.getReportId());
            reportObj.put("reportItemId", report.getReportItemId());
            reportObj.put("reportItem", report.getReportItem());
            reportObj.put("reportType", report.getReportType());
            reportObj.put("reportReason", report.getReportReason());
            reportObj.put("reportUserId", report.getUsers().getUserId());
            reportObj.put("reportStatus", String.valueOf(report.getReportStatus()));

            Activities activity = activitiesService.findActById(report.getReportItemId());
            if (activity != null) {
                reportObj.put("reportedContent", activity.getActivitiesTitle());
            } else {
                reportObj.put("activityTitle", "Not found this activity");
            }

            return reportObj;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reportList.toString());
    }

    @GetMapping("/report/list/actComments")
    public ResponseEntity<?> getCommentReports() {
        List<Report> reports = reportService.findReportsByItem("AC");
        List<JSONObject> reportList = reports.stream().map(report -> {
            JSONObject reportObj = new JSONObject();
            reportObj.put("reportId", report.getReportId());
            reportObj.put("reportItemId", report.getReportItemId());
            reportObj.put("reportItem", report.getReportItem());
            reportObj.put("reportType", report.getReportType());
            reportObj.put("reportReason", report.getReportReason());
            reportObj.put("reportUserId", report.getUsers().getUserId());
            reportObj.put("reportStatus", String.valueOf(report.getReportStatus()));

            ActivitiesComment comment = actCommentService.findCommentById(report.getReportItemId());
            if (comment != null) {
                reportObj.put("reportedContent", comment.getMessageContent());
                reportObj.put("commentUserId", comment.getUserId());
                reportObj.put("commentUserName", comment.getUserName());
                reportObj.put("associatedActivityId", comment.getActivities().getActivitiesId());
            } else {
                reportObj.put("reportedContent", "Not found this comment");
            }

            return reportObj;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reportList.toString());
    }
    
    private void sendReportApprovedNotification(String reportItem, Integer reportItemId) {
        String notificationTitle = "檢舉成立通知";
        String notificationContent;
        Users recipient;

        if ("A".equals(reportItem)) {
            Activities activity = activitiesService.findActById(reportItemId);
            if (activity != null) {
                recipient = activity.getUsers();
                notificationContent = recipient.getUsersProfile().getUserName() + "您好，您舉辦的活動 " + 
                    activity.getActivitiesTitle() + " 已被他人檢舉，經管理員審核後認定檢舉成立，故將該活動下架，若有疑問請與管理員聯繫";
            } else {
                return;
            }
        } else if ("AC".equals(reportItem)) {
            ActivitiesComment comment = actCommentService.findCommentById(reportItemId);
            if (comment != null) {
                recipient = comment.getUsers();
                notificationContent = recipient.getUsersProfile().getUserName() + "您好，您的留言 \"" + 
                    comment.getMessageContent() + "\" 已被他人檢舉，經管理員審核後認定檢舉成立，故將該留言下架，若有疑問請與管理員聯繫";
            } else {
                return;
            }
        } else {
            return;
        }
        
        Notification notification = new Notification();
        notification.setNotificationTitle(notificationTitle);
        notification.setNotificationContent(notificationContent);
        notification.setUsers(recipient);

        notiService.saveNotification(notification);
    }
    
    
}
