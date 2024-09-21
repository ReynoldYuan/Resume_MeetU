package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeesRepository extends JpaRepository<Attendees, Integer> {

	
	boolean existsByActivitiesAndUsers(Activities activities, Users users);
	
	List<Attendees> findByUsers(Users user);
	
	Attendees findByActivitiesAndUsersUserId(Activities activities, Integer userId);
	
	Attendees findByActivitiesActivitiesIdAndUsersUserId(Integer activityId, Integer userId);
	
	 List<Attendees> findByActivities(Activities activities);
	 
	 List<Attendees> findByUsersUserIdAndIsCompletedTrue(Integer userId);
	 
	 Attendees findByActivitiesAndUsers(Activities activities, Users users);
	 }
