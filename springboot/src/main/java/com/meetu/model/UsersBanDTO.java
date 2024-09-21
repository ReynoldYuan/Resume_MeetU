package com.meetu.model;

public class UsersBanDTO {

	private Integer userId;
	
	private Integer banedUserId;
	
	private String banedUserName;

	

	public UsersBanDTO(Integer userId, Integer banedUserId, String banedUserName) {
		this.userId = userId;
		this.banedUserId = banedUserId;
		this.banedUserName = banedUserName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBanedUserId() {
		return banedUserId;
	}

	public void setBanedUserId(Integer banedUserId) {
		this.banedUserId = banedUserId;
	}

	public String getBanedUserName() {
		return banedUserName;
	}

	public void setBanedUserName(String banedUserName) {
		this.banedUserName = banedUserName;
	}
	
	
	
}
