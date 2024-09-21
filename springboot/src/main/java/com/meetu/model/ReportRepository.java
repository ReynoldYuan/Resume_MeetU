package com.meetu.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ReportRepository extends JpaRepository<Report, Integer> {

	List<Report> findByUsers_UserId(Integer userId);
	
	boolean existsByUsers_UserIdAndReportItemIdAndReportItemAndReportStatus(
	        Integer userId, Integer reportItemId, String reportItem, Character reportStatus);	
	
	List<Report> findByUsers_UserIdAndReportStatus(Integer userId, Character reportStatus);

	void deleteByUsers_UserIdAndReportItemIdAndReportItem(Integer userId, Integer reportItemId, String reportItem);

	List<Report> findByReportItem(String reportItem);
	
	List<Report> findByReportItemAndReportItemId(String reportItem, Integer reportItemId);
	
}
