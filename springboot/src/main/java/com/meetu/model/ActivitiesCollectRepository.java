package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivitiesCollectRepository extends JpaRepository<ActivitiesCollect, ActivitiesCollectId> {

	
	@Query("from ActivitiesCollect a where a.users.userId = :uuu ")
	List<ActivitiesCollect> findByUsersId(@Param("uuu") Integer usersId);
	
	boolean existsByUsers_UserIdAndActivities_ActivitiesId(Integer userId, Integer activitiesId);
	
	List<ActivitiesCollect> findByActivities_ActivitiesId(Integer activitiesId);

    @Query("SELECT COUNT(ac) FROM ActivitiesCollect ac WHERE ac.activities.activitiesId = :activitiesId")
    Integer countByActivitiesId(@Param("activitiesId") Integer activitiesId);
}
