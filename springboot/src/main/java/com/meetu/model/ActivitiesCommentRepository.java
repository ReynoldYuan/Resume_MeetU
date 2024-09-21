package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivitiesCommentRepository extends JpaRepository<ActivitiesComment, Integer> {

	@Query("from ActivitiesComment where activities.activitiesId = :uuu ")
	List<ActivitiesComment> findByActivitiesId(@Param("uuu") Integer activitiesId);
	
	 @Query("SELECT ac FROM ActivitiesComment ac WHERE ac.activities.activitiesId = :aid AND ac.activitiesReportStatus = 'N' ORDER BY ac.messageTime DESC")
	    List<ActivitiesComment> findByActivityIdAndReportStatusN(@Param("aid") Integer aid);
	}
