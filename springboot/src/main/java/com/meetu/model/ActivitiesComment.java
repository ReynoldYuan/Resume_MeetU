package com.meetu.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "activities_comment")
public class ActivitiesComment {

	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activitiesCommentId;
	
	private String activitiesReportStatus;
	
    @ManyToOne
    @JoinColumn(name = "fk_activitiesId")
    @JsonIgnore
    private Activities activities;
	
	@ManyToOne
    @JoinColumn(name = "fk_userId")
	@JsonIgnore
    private Users users;
	
	private String messageContent;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss EEEE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date messageTime;
	
	@PrePersist // 當物件要進入 Persistent 狀態以前，先做這個方法
	public void onCreate() {
		if (messageTime == null) {
			messageTime = new Date();
		}
	}
	
	public ActivitiesComment() {
	}

	public Integer getActivitiesCommentId() {
		return activitiesCommentId;
	}

	public void setActivitiesCommentId(Integer activitiesCommentId) {
		this.activitiesCommentId = activitiesCommentId;
	}

	public String getActivitiesReportStatus() {
		return activitiesReportStatus;
	}

	public void setActivitiesReportStatus(String activitiesReportStatus) {
		this.activitiesReportStatus = activitiesReportStatus;
	}

	public Activities getActivities() {
		return activities;
	}

	public void setActivities(Activities activities) {
		this.activities = activities;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public Date getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(Date messageTime) {
		this.messageTime = messageTime;
	}


	public Integer getUserId() {
        return users != null ? users.getUserId() : null;
    }

    // 添加一個新方法來獲取用戶名
    public String getUserName() {
        return users != null && users.getUsersProfile() != null ? users.getUsersProfile().getUserName() : null;
    }
    
    public String getUserPics() {
        return users != null && users.getUsersProfile() != null ? users.getUsersProfile().getUserPics() : null;
    }

}
