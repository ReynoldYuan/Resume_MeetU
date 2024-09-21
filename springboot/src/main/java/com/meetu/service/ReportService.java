package com.meetu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.Activities;
import com.meetu.model.Report;
import com.meetu.model.ReportRepository;
import com.meetu.model.Users;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReportService {

	@Autowired
	private ReportRepository reportRepo;
	
	@Autowired
	private ActivitiesService actService;
	
	public Report saveReport(Report report) {
		return reportRepo.save(report);
	}
	
	public void deleteReport(Integer id) {
		reportRepo.deleteById(id);
	}
	
	public Report findReportById(Integer id) {
		Optional<Report> optional = reportRepo.findById(id);
	
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public List<Report> findAllReports(){
		return reportRepo.findAll();
	}
	
	public List<Report> findReportByUserId(Integer userId){
		return reportRepo.findByUsers_UserId(userId);
	}
	
	public boolean existsReport(Integer reportUserId, Integer reportedUserId, String reportItem) {
        return reportRepo.existsByUsers_UserIdAndReportItemIdAndReportItemAndReportStatus(reportUserId, reportedUserId, reportItem, 'P');
    }
	
	public List<Report> findUserReports(Integer userId) {
        return reportRepo.findByUsers_UserIdAndReportStatus(userId, 'P');
    }
    
    public void deleteReport(Integer userId, Integer reportItemId, String reportItem) {
        reportRepo.deleteByUsers_UserIdAndReportItemIdAndReportItem(userId, reportItemId, reportItem);
    }
    
    public List<Report> findReportsByItem(String reportItem) {
        return reportRepo.findByReportItem(reportItem);
    }
    
    public Report updateReportStatus(Integer reportId, Character newStatus) {
        Optional<Report> reportOptional = reportRepo.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setReportStatus(newStatus);
            return reportRepo.save(report);
        }
        return null;
    }
    

}
