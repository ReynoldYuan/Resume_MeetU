package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRespository extends JpaRepository<Notification, Integer>{

	List<Notification> findByUsers_UserId(Integer userId);
	
	@Query(value = "SELECT Top 5 * FROM notification WHERE notificationUserId = :userId ORDER BY notificationTime DESC", nativeQuery = true)
    List<Notification> findTop5ByUserId(@Param("userId") Integer userId);
	
}
