package com.meetu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "report")
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reportId;
	private String reportItem;
	private Integer reportItemId;
	private String reportType;
	private String reportReason;
	private Character reportStatus;
	
	@ManyToOne
	@JoinColumn(name = "reportUserId")
	private Users users;
	
	@PrePersist
	public void onCreate() {
		if(reportStatus == null) {
			reportStatus = 'P';
		}
	}
	
	@PreUpdate
	public void preUpdate() {
		if(reportStatus == null) {
			reportStatus = 'P';
		}
	}

	public Report() {
	}

	public Report(Integer reportId, String reportItem, Integer reportItemId, String reportType, String reportReason, Users users) {
		this.reportId = reportId;
		this.reportItem = reportItem;
		this.reportItemId = reportItemId;
		this.reportType = reportType;
		this.reportReason = reportReason;
		this.users = users;
	}


	public Report(Users users, String reportItem,  Integer reportItemId, String reportType, String reportReason ) {
		this.reportItem = reportItem;
		this.reportItemId = reportItemId;
		this.reportType = reportType;
		this.reportReason = reportReason;
		this.users = users;
	}

	
	
	public Integer getReportId() {
		return reportId;
	}


	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}


	public String getReportItem() {
		return reportItem;
	}


	public void setReportItem(String reportItem) {
		this.reportItem = reportItem;
	}


	public Integer getReportItemId() {
		return reportItemId;
	}


	public void setReportItemId(Integer reportItemId) {
		this.reportItemId = reportItemId;
	}


	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
	}


	public String getReportReason() {
		return reportReason;
	}


	public void setReportReason(String reportReason) {
		this.reportReason = reportReason;
	}


	public char getReportStatus() {
		return reportStatus;
	}


	public void setReportStatus(char reportStatus) {
		this.reportStatus = reportStatus;
	}


	public Users getUsers() {
		return users;
	}


	public void setUsers(Users users) {
		this.users = users;
	}

	
	
}
