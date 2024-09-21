package com.meetu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.Activities;
import com.meetu.model.ActivitiesCollect;
import com.meetu.model.ActivitiesCollectId;
import com.meetu.model.ActivitiesCollectRepository;
import com.meetu.model.ActivitiesRepository;
import com.meetu.model.Users;
import com.meetu.model.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class ActivitiesCollectService {
	
	@Autowired
	private UsersRepository usersRepo;
	
	@Autowired
	private ActivitiesRepository actRepo;
	
	@Autowired
	private ActivitiesCollectRepository actCollectRepo;
	
	@Transactional
	public ActivitiesCollect addToCollect(Integer usersId,Integer activitiesId) {
		
		Optional<Users> optional = usersRepo.findById(usersId);
		Users users = optional.get();
		
		Optional<Activities> optional2 = actRepo.findById(activitiesId);
		
		Activities activities = optional2.get();
		
		ActivitiesCollectId activitiesCollectId = new ActivitiesCollectId();
		activitiesCollectId.setCollectUserId(usersId);
		activitiesCollectId.setCollectActId(activitiesId);
		
		ActivitiesCollect activitiesCollect = new ActivitiesCollect();
		activitiesCollect.setActCollectId(activitiesCollectId);
		activitiesCollect.setUsers(users);
		activitiesCollect.setActivities(activities);
		
		return actCollectRepo.save(activitiesCollect);
	}
	
	public List<ActivitiesCollect> findActCollectByUserId(Integer userId) {
		return actCollectRepo.findByUsersId(userId);
	}
	
	@Transactional
	public void deleteCollect(Integer usersId, Integer activitiesId) {
		ActivitiesCollectId activitiesCollectId = new ActivitiesCollectId();
		activitiesCollectId.setCollectUserId(usersId);
		activitiesCollectId.setCollectActId(activitiesId);
		
		actCollectRepo.deleteById(activitiesCollectId);
	}
	
	public boolean isActivityCollectedByUser(Integer userId, Integer activitiesId) {
        return actCollectRepo.existsByUsers_UserIdAndActivities_ActivitiesId(userId, activitiesId);
    }
	
	public Map<String, Object> getActivityCollectorsInfo(Integer activitiesId) {
        List<ActivitiesCollect> collectors = actCollectRepo.findByActivities_ActivitiesId(activitiesId);
        
        List<Map<String, Object>> collectorDetails = new ArrayList<>();
        for (ActivitiesCollect collect : collectors) {
            Map<String, Object> details = new HashMap<>();
            details.put("userId", collect.getUsers().getUserId());
            details.put("userName", collect.getUsers().getUsersProfile().getUserName());
            details.put("userPics", collect.getUsers().getUsersProfile().getUserPics());
            collectorDetails.add(details);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalCollectors", collectors.size());
        response.put("collectors", collectorDetails);

        return response;
    }

}
