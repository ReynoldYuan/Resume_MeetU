package com.meetu.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivitiesRepository extends JpaRepository<Activities, Integer> {

	@Query("from Activities where users.userId = :uuu ")
	List<Activities> findByusersId(@Param("uuu") Integer usersId);
	
	List<Activities> findByUsers(Users host);
	
	@Query("from Activities where activitiesReportStatus = 'N' and activitiesEndDate > :currentTime")
    List<Activities> findValidActivities(@Param("currentTime") LocalDateTime currentTime);
	
	
    @Query("from Activities where users.userId = :userId and activitiesEndDate > :currentTime and activitiesReportStatus != 'D'")
    List<Activities> findUpcomingActivities(@Param("userId") Integer userId, @Param("currentTime") LocalDateTime currentTime);

    
    @Query("from Activities where users.userId = :userId and activitiesEndDate <= :currentTime and activitiesReportStatus != 'D'")
    List<Activities> findPastActivities(@Param("userId") Integer userId, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT a FROM Activities a WHERE " +
    	       "(LOWER(a.activitiesTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
    	       "LOWER(a.activitiesLocation) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
    	       "LOWER(a.activitiesContent) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
    	       "a.activitiesEndDate > CURRENT_TIMESTAMP AND " +
    	       "a.activitiesReportStatus NOT IN ('R', 'D')")
    	List<Activities> searchActivities(@Param("query") String query);
}
