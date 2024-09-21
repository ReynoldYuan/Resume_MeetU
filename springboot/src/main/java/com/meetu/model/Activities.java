package com.meetu.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "activities")
public class Activities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activitiesId;

    private String activitiesReportStatus;

    private String activitiesType;

    @ManyToOne
    @JoinColumn(name = "hostId")
    @JsonManagedReference
    private Users users;
    
    @OneToMany(mappedBy = "activities", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivitiesComment> comments;
    
    @OneToMany(mappedBy = "activities", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ActivitiesCollect> collects;

    @Lob
    private byte[] activitiesPics;  //string 存路徑

    private String activitiesTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private LocalDateTime activitiesStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private LocalDateTime activitiesEndDate;

    private String activitiesLocation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private LocalDateTime activitiesVerifyDate;

    private String activitiesContent;

    private String activitiesSharing;

    private Integer activitiesAmt;

    private Integer activitiesMaxPeo;

    private Integer activitiesTagId;
    
    @OneToMany(mappedBy = "activities", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Attendees> attendees;


    public Activities() {
    }

    public Integer getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(Integer activitiesId) {
        this.activitiesId = activitiesId;
    }

    public String getActivitiesReportStatus() {
        return activitiesReportStatus;
    }

    public void setActivitiesReportStatus(String activitiesReportStatus) {
        this.activitiesReportStatus = activitiesReportStatus;
    }

    public String getActivitiesType() {
        return activitiesType;
    }

    public void setActivitiesType(String activitiesType) {
        this.activitiesType = activitiesType;
    }


    public List<Attendees> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<Attendees> attendees) {
		this.attendees = attendees;
	}

	public byte[] getActivitiesPics() {
        return activitiesPics;
    }

    public void setActivitiesPics(byte[] activitiesPics) {
        this.activitiesPics = activitiesPics;
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

    public int getActivitiesAmt() {
        return activitiesAmt;
    }

    public void setActivitiesAmt(int activitiesAmt) {
        this.activitiesAmt = activitiesAmt;
    }

    public int getActivitiesMaxPeo() {
        return activitiesMaxPeo;
    }

    public void setActivitiesMaxPeo(int activitiesMaxPeo) {
        this.activitiesMaxPeo = activitiesMaxPeo;
    }

    public Integer getActivitiesTagId() {
        return activitiesTagId;
    }

    public void setActivitiesTagId(Integer activitiesTagId) {
        this.activitiesTagId = activitiesTagId;
    }

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}
}
