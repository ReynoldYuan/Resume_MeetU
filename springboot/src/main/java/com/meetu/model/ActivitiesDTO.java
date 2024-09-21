package com.meetu.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ActivitiesDTO {
    private String activitiesType;
    private String activitiesTitle;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime activitiesStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime activitiesEndDate;

    private String activitiesLocation;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime activitiesVerifyDate;

    private String activitiesContent;
    private String activitiesSharing;
    private Integer activitiesAmt;
    private Integer activitiesMaxPeo;
    private Integer activitiesTagId;
    private MultipartFile activitiesPics;
    
    
    
    
    
	public ActivitiesDTO() {
	}
	
	
	public String getActivitiesType() {
		return activitiesType;
	}
	public void setActivitiesType(String activitiesType) {
		this.activitiesType = activitiesType;
	}
	public String getActivitiesTitle() {
		return activitiesTitle;
	}
	public void setActivitiesTitle(String activitiesTitle) {
		this.activitiesTitle = activitiesTitle;
	}
	public LocalDateTime getActivitiesStartDate() {
		return activitiesStartDate;
	}
	public void setActivitiesStartDate(LocalDateTime activitiesStartDate) {
		this.activitiesStartDate = activitiesStartDate;
	}
	public LocalDateTime getActivitiesEndDate() {
		return activitiesEndDate;
	}
	public void setActivitiesEndDate(LocalDateTime activitiesEndDate) {
		this.activitiesEndDate = activitiesEndDate;
	}
	public String getActivitiesLocation() {
		return activitiesLocation;
	}
	public void setActivitiesLocation(String activitiesLocation) {
		this.activitiesLocation = activitiesLocation;
	}
	public LocalDateTime getActivitiesVerifyDate() {
		return activitiesVerifyDate;
	}
	public void setActivitiesVerifyDate(LocalDateTime activitiesVerifyDate) {
		this.activitiesVerifyDate = activitiesVerifyDate;
	}
	public String getActivitiesContent() {
		return activitiesContent;
	}
	public void setActivitiesContent(String activitiesContent) {
		this.activitiesContent = activitiesContent;
	}
	public String getActivitiesSharing() {
		return activitiesSharing;
	}
	public void setActivitiesSharing(String activitiesSharing) {
		this.activitiesSharing = activitiesSharing;
	}
	public Integer getActivitiesAmt() {
		return activitiesAmt;
	}
	public void setActivitiesAmt(Integer activitiesAmt) {
		this.activitiesAmt = activitiesAmt;
	}
	public Integer getActivitiesMaxPeo() {
		return activitiesMaxPeo;
	}
	public void setActivitiesMaxPeo(Integer activitiesMaxPeo) {
		this.activitiesMaxPeo = activitiesMaxPeo;
	}
	public Integer getActivitiesTagId() {
		return activitiesTagId;
	}
	public void setActivitiesTagId(Integer activitiesTagId) {
		this.activitiesTagId = activitiesTagId;
	}
	public MultipartFile getActivitiesPics() {
		return activitiesPics;
	}
	public void setActivitiesPics(MultipartFile activitiesPics) {
		this.activitiesPics = activitiesPics;
	}

}
