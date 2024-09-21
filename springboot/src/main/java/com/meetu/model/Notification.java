package com.meetu.model;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "notification")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationId;
	private String notificationTitle;
	private String notificationContent;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss EEEE") 
	@Temporal(TemporalType.TIMESTAMP)
	private Date notificationTime;
	private Character notificationRead;
	
	@ManyToOne
	@JoinColumn(name = "notificationUserId")
	@JsonIgnore
	private Users users;
	
    private Character isGlobal;
	
	@PrePersist 
	public void onCreate() {
		if(notificationTime == null) {
			notificationTime = new Date();
		}
		if(notificationRead == null) {
			notificationRead = '0';
		}
		if(isGlobal == null) {
            isGlobal = '0';
        }
	}
	
	@PreUpdate
    public void preUpdate() {
		if(notificationTime == null) {
			notificationTime = new Date();
		}
        if(notificationRead == null) {
			notificationRead = '0';
		}
        if(isGlobal == null) {
            isGlobal = '0';
        }
    }
	
	@JsonProperty("userName")
    public String getUserName() {
        return users != null && users.getUsersProfile() != null ? users.getUsersProfile().getUserName() : null;
    }
	
	@JsonProperty("isGlobalNotification")
    public boolean isGlobalNotification() {
        return isGlobal != null && isGlobal == '1';
    }
	
	public Notification() {
	}

	public Integer getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	public String getNotificationContent() {
		return notificationContent;
	}

	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}

	public Date getNotificationTime() {
		return notificationTime;
	}

	public void setNotificationTime(Date notificationTime) {
		this.notificationTime = notificationTime;
	}

	public char getNotificationRead() {
		return notificationRead;
	}

	public void setNotificationRead(Character notificationRead) {
		this.notificationRead = notificationRead;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Notification(Integer notificationId, String notificationTitle, String notificationContent, Users users) {
		this.notificationId = notificationId;
		this.notificationTitle = notificationTitle;
		this.notificationContent = notificationContent;
		this.users = users;
		this.isGlobal = '0';
	}

	public Notification(String notificationTitle, String notificationContent, Users users) {
		this.notificationTitle = notificationTitle;
		this.notificationContent = notificationContent;
		this.users = users;
		this.isGlobal = '0';
	}

	public Character getIsGlobal() {
		return isGlobal;
	}

	public void setIsGlobal(Character isGlobal) {
		this.isGlobal = isGlobal;
	}

	
}
