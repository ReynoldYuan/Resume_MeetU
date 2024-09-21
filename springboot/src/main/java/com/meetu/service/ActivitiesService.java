package com.meetu.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.meetu.model.Activities;
import com.meetu.model.ActivitiesRepository;
import com.meetu.model.Users;

import jakarta.transaction.Transactional;

@Service
public class ActivitiesService {

	@Autowired
	private ActivitiesRepository actRepo;

	public Activities saveActivities(Activities act) {
		
		return actRepo.save(act);
		
	}

	public Page<Activities> findByPage(Integer pageNumber) {
		Pageable pgb = PageRequest.of(pageNumber - 1, 3);
		Page<Activities> page = actRepo.findAll(pgb);
		return page;
	}

	public List<Activities> findAllAct() {
		return actRepo.findAll();
	}

	public List<Activities> findActByUserId(Integer userId) {
		return actRepo.findByusersId(userId);
	}

	public boolean deleteActById(Integer activitiesId) {
		actRepo.deleteById(activitiesId);
		return true;
	}

	public Activities findActById(Integer activitiesId) {

		Optional<Activities> optional = actRepo.findById(activitiesId);

		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Transactional
	public Activities updateActivities(Integer activitiesId, String activitiesType, MultipartFile activitiesPics,
			String activitiesTitle, LocalDateTime activitiesStartDate, LocalDateTime activitiesEndDate,
			String activitiesLocation, LocalDateTime activitiesVerifyDate, String activitiesContent,
			String activitiesSharing, Integer activitiesAmt, Integer activitiesMaxPeo) throws IOException {
		Optional<Activities> optional = actRepo.findById(activitiesId);

		if (optional.isPresent()) {
			Activities act = optional.get();
			act.setActivitiesType(activitiesType);
//			act.setActivitiesPics(activitiesPics.getBytes());
			act.setActivitiesTitle(activitiesTitle);
			act.setActivitiesStartDate(activitiesStartDate);
			act.setActivitiesEndDate(activitiesEndDate);
			act.setActivitiesLocation(activitiesLocation);
			act.setActivitiesVerifyDate(activitiesVerifyDate);
			act.setActivitiesContent(activitiesContent);
			act.setActivitiesSharing(activitiesSharing);
			act.setActivitiesAmt(activitiesAmt);
			act.setActivitiesMaxPeo(activitiesMaxPeo);

			return act;

		}
		return null;

	}

	public List<Activities> findActivitiesByHost(Users host) {
	    return actRepo.findByUsers(host);
	}

	
	// 更新活動檢舉狀態
	public Activities updateActivitiesReportStatus(Activities activity) {
        if (activity == null || activity.getActivitiesId() == null) {
            throw new IllegalArgumentException("活動或活動ID不能為空");
        }
        
        Activities existingActivity = actRepo.findById(activity.getActivitiesId()).orElse(null);
        if (existingActivity == null) {
            throw new IllegalArgumentException("找不到指定的活動ID");
        }
        
        existingActivity.setActivitiesReportStatus(activity.getActivitiesReportStatus());
        return actRepo.save(existingActivity);
    }
	
	 public List<Activities> findValidActivities() {
	        LocalDateTime now = LocalDateTime.now();
	        return actRepo.findValidActivities(now);
	    }
	 
	 public List<Activities> findUpcomingActivities(Integer userId) {
	        LocalDateTime now = LocalDateTime.now();
	        return actRepo.findUpcomingActivities(userId, now);
	    }

	    public List<Activities> findPastActivities(Integer userId) {
	        LocalDateTime now = LocalDateTime.now();
	        return actRepo.findPastActivities(userId, now);
	    }
	
	    public List<Activities> searchActivities(String query) {
	        return actRepo.searchActivities(query);
	    }
}
