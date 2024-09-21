package com.meetu.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.meetu.model.Activities;
import com.meetu.model.ActivitiesComment;
import com.meetu.model.Users;
import com.meetu.service.ActivitiesCommentService;
import com.meetu.service.ActivitiesService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ActivitiesController {

	@Autowired
	private ActivitiesService actService;
	
	@Autowired
	private ActivitiesCommentService commentService;

	@GetMapping("/activities/add")
	public String addActivities() {
		return "activities/addActPage";
	}

	@PostMapping("/activities/addPost")
	public String postAct(
			@RequestParam String activitiesType, 
			@RequestParam MultipartFile activitiesPics, 
			@RequestParam String activitiesTitle,
			@RequestParam LocalDateTime activitiesStartDate,
			@RequestParam LocalDateTime activitiesEndDate,
			@RequestParam String activitiesLocation, 
			@RequestParam LocalDateTime activitiesVerifyDate,
			@RequestParam String activitiesContent, 
			@RequestParam String activitiesSharing,
			@RequestParam Integer activitiesAmt, 
			@RequestParam Integer activitiesMaxPeo,
//			@RequestParam Integer activitiesTagId, 
			HttpSession httpSession, 
			Model model) throws IOException {
		Users users = (Users) httpSession.getAttribute("loginUser");

		//有登入狀態
		if (users != null) {


			Activities newAct = new Activities();
			//預設值為"N" normal
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
//		newAct.setActivitiesTagId(activitiesTagId);

			actService.saveActivities(newAct);

			model.addAttribute("okMsg", "新增成功有登入");

			//無登入狀態
		} else {

			model.addAttribute("okMsg", "請先登入");
			return "users/loginPage";

		}
		return "activities/addActPage";
	}
	
//	所有活動
	@GetMapping("/activities/showAll")
	public String showActivities(@RequestParam(value="p", defaultValue = "1") Integer pageNum, Model model) {
		org.springframework.data.domain.Page<Activities> page = actService.findByPage(pageNum);
		
		model.addAttribute("page", page);
		
		return "activities/showActPage";
	}
	
//	活動照片download	
	@GetMapping("/photos/download")
	public ResponseEntity<byte[]> download(@RequestParam Integer id){
		Activities activities = actService.findActById(id);
		
		if(activities != null) {
			
			byte[] photoFile = activities.getActivitiesPics();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			                                 // 回應的資料, header, status code
			return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);
		}
		                               // 單純回傳 404
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	
	
//	我的活動及列出各活動的留言
	@GetMapping("/activities/userslist")
	public String listActivities(Model model, HttpSession httpSession) {

		Users users = (Users) httpSession.getAttribute("loginUser");
		if (users == null) {
			return"users/loginPage";
		}else {
		Integer userId = users.getUserId();
		
		List<Activities> usersAct = actService.findActByUserId(userId);
		
		
		 Map<Integer, List<ActivitiesComment>> commentsByActivityId = new HashMap<>();
	        for (Activities act : usersAct) {
	            List<ActivitiesComment> comments = commentService.findCommentsByActivityId(act.getActivitiesId());
	            commentsByActivityId.put(act.getActivitiesId(), comments);
	        }

	        model.addAttribute("ActList", usersAct);
	        model.addAttribute("commentsByActivityId", commentsByActivityId);
	        
	        return "activities/showUsersActPage";
	    }
	}
	@GetMapping("/activities/delete")
	public String deleteAct(@RequestParam Integer activitiesId) {
		actService.deleteActById(activitiesId);
		return "redirect:/activities/userslist";
	}
	

	@GetMapping("/activities/update")
	public String editAct(@RequestParam Integer activitiesId,Model model, HttpSession httpSession) {
		Activities activities = actService.findActById(activitiesId);
		
		Integer actUsersId = activities.getUsers().getUserId();
		
		if (actUsersId != null) {
		 Users loginUser=(Users)httpSession.getAttribute("loginUser");
		 Integer userId = loginUser.getUserId();
			if (userId.equals(actUsersId)) {
				
				model.addAttribute("activities",activities);
				return "activities/editActPage";
				
			}
		}
		return null;
	}
	@PostMapping("/activities/updatePost")
	public String editActPost(
			@RequestParam Integer activitiesId,
			@RequestParam String activitiesType, 
			@RequestParam MultipartFile activitiesPics, 
			@RequestParam String activitiesTitle,
			@RequestParam LocalDateTime activitiesStartDate,
			@RequestParam LocalDateTime activitiesEndDate,
			@RequestParam String activitiesLocation, 
			@RequestParam LocalDateTime activitiesVerifyDate,
			@RequestParam String activitiesContent, 
			@RequestParam String activitiesSharing,
			@RequestParam Integer activitiesAmt, 
			@RequestParam Integer activitiesMaxPeo,HttpSession httpsession) throws IOException {
		
		Activities activities = actService.findActById(activitiesId);
		
		Integer usersId = activities.getUsers().getUserId();
		
		 Users loginUser=(Users)httpsession.getAttribute("loginUser");
		 Integer loginUserId = loginUser.getUserId();
		
		if (loginUserId.equals(usersId)) {
			activities.setActivitiesType(activitiesType);
			activities.setActivitiesPics(activitiesPics.getBytes());
			activities.setActivitiesTitle(activitiesTitle);
			activities.setActivitiesStartDate(activitiesStartDate);
			activities.setActivitiesEndDate(activitiesEndDate);
			activities.setActivitiesLocation(activitiesLocation);
			activities.setActivitiesVerifyDate(activitiesVerifyDate);
			activities.setActivitiesContent(activitiesContent);
			activities.setActivitiesSharing(activitiesSharing);
			activities.setActivitiesAmt(activitiesAmt);
			activities.setActivitiesMaxPeo(activitiesMaxPeo);
			
			actService.saveActivities(activities);
			
			return "redirect:/activities/userslist";
			
		}
		return null;
	}
}
