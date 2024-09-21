package com.meetu.model;

import java.util.Date;


public class NotificationDTO{
	
	private Integer notificationId;
    private Integer notificationUserId;
    private String notificationTitle;
    private String notificationContent;
    private Date notificationTime;
    private Character isGlobal;
    
	public NotificationDTO() {
	}


	public NotificationDTO(Integer notificationUserId, String notificationTitle, String notificationContent, Date notificationTime) {
		this.notificationUserId = notificationUserId;
		this.notificationTitle = notificationTitle;
		this.notificationContent = notificationContent;
		this.notificationTime = notificationTime;
	}


	public NotificationDTO(Integer notificationId, Integer notificationUserId, String notificationTitle,
			String notificationContent, Date notificationTime) {
		this.notificationId = notificationId;
		this.notificationUserId = notificationUserId;
		this.notificationTitle = notificationTitle;
		this.notificationContent = notificationContent;
		this.notificationTime = notificationTime;
	}


	public Integer getNotificationId() {
		return notificationId;
	}


	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}


	public Integer getNotificationUserId() {
		return notificationUserId;
	}


	public void setNotificationUserId(Integer notificationUserId) {
		this.notificationUserId = notificationUserId;
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


	@Override
	public String toString() {
		return "NotificationDTO [notificationId=" + notificationId + ", notificationUserId=" + notificationUserId
				+ ", notificationTitle=" + notificationTitle + ", notificationContent=" + notificationContent + "]";
	}


	public Date getNotificationTime() {
		return notificationTime;
	}


	public void setNotificationTime(Date notificationTime) {
		this.notificationTime = notificationTime;
	}


	public Character getIsGlobal() {
		return isGlobal;
	}


	public void setIsGlobal(Character character) {
		this.isGlobal = character;
	}
    
}
