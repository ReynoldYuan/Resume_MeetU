package com.meetu.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.Activities;
import com.meetu.model.Attendees;
import com.meetu.model.AttendeesRepository;
import com.meetu.model.Users;

import jakarta.transaction.Transactional;

@Service
public class AttendeesService {

	@Autowired
	private AttendeesRepository atenRepo;
	
	  @Autowired
	    private UserService userService;  // 添加這行
	  

	  @Transactional
	  public Attendees saveAttendee(Attendees attendees) {
//	      System.out.println("開始保存參與者信息");
//	      
//	      Integer userId = attendees.getUsers().getUserId();
//	      Users user = attendees.getUsers(); // 直接使用傳入的用戶對象，不再重新查詢
//	      
//	      System.out.println("保存前 - 用戶ID: " + userId);
//	      System.out.println("保存前 - 用戶圖片路徑: " + user.getUsersProfile().getUserPics());
//
		   System.out.println("保存參加者之前: " + attendees);
	      Attendees savedAttendee = atenRepo.save(attendees);
	      atenRepo.flush(); // 添加這行確保立即寫入數據庫
	      System.out.println("保存參加者之後: " + savedAttendee);

//
//	      System.out.println("保存後 - 用戶ID: " + savedAttendee.getUsers().getUserId());
//	      System.out.println("保存後 - 用戶圖片路徑: " + savedAttendee.getUsers().getUsersProfile().getUserPics());

	      return savedAttendee;
	  }
	public List<Attendees> findAllAttendees() {
		return atenRepo.findAll();

	}

	public Attendees findAttendeeById(Integer id) {
		Optional<Attendees> optional = atenRepo.findById(id);

		if (optional.isPresent()) {
			Attendees attendees = optional.get();
			return attendees;
		}
		return null;
	}

	public boolean deleteAttenById(Integer id) {
		if (atenRepo.existsById(id)) {
			atenRepo.deleteById(id);
			return true;
		}
		return false;
	}

	public boolean existsByActivitiesAndUsers(Activities activities, Users users) {
		return atenRepo.existsByActivitiesAndUsers(activities, users);
	}

	public List<Attendees> findByUserWithApprovalStatus(Users user) {
		return atenRepo.findByUsers(user);

	}
//	public Attendees findAttendeeByActivityAndUserId(Activities activity, Integer userId) {
//	    return atenRepo.findByActivitiesAndUsersUserId(activity, userId);
//	}
	public Attendees findAttendeeByActivityAndUserId(Activities activity, Integer userId) {
        return atenRepo.findByActivitiesActivitiesIdAndUsersUserId(activity.getActivitiesId(), userId);
    }
	
	@Transactional
	public boolean cancelRegistration(Integer entryId, Integer userId) {
	    Optional<Attendees> attendeeOpt = atenRepo.findById(entryId);
	    if (attendeeOpt.isPresent() && attendeeOpt.get().getUsers().getUserId().equals(userId)) {
	        atenRepo.deleteById(entryId);
	        return true;
	    }
	    return false;
	}
	 public List<Attendees> findByActivity(Activities activity) {
	        return atenRepo.findByActivities(activity);
	    }
	 
	 public List<Map<String, Object>> findPastActivitiesByUserId(Integer userId) {
	        List<Attendees> completedAttendees = atenRepo.findByUsersUserIdAndIsCompletedTrue(userId);
	        List<Map<String, Object>> pastActivities = new ArrayList<>();

	        for (Attendees attendee : completedAttendees) {
	            Activities activity = attendee.getActivities();
	            if (activity != null) {
	                Map<String, Object> activityDetails = new HashMap<>();
	                activityDetails.put("entryId", attendee.getEntryId());
	                activityDetails.put("activityId", activity.getActivitiesId());
	                activityDetails.put("activityName", activity.getActivitiesTitle());
	                activityDetails.put("activitiesStartDate", activity.getActivitiesStartDate());
	                activityDetails.put("activitiesEndDate", activity.getActivitiesEndDate());
	                activityDetails.put("activitiesReportStatus", activity.getActivitiesReportStatus());
	                activityDetails.put("activitiesLocation", activity.getActivitiesLocation());
	                activityDetails.put("registrationDate", attendee.getRegisteredAt());
	                activityDetails.put("isApproved", attendee.isApproved());
	                activityDetails.put("isCompleted", attendee.isCompleted());
	                pastActivities.add(activityDetails);
	            }
	        }

	        return pastActivities;
	    }
	 
	 public Attendees findByActivitiesAndUsers(Activities activities, Users users) {
	     return atenRepo.findByActivitiesAndUsers(activities, users);
	 }
}
