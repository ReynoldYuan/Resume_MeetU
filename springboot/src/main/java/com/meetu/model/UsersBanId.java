package com.meetu.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class UsersBanId implements Serializable{
	
	private Integer userId;
	private Integer banedUserId;

	public UsersBanId() {
	}

	@Override
	public int hashCode() {
		return Objects.hash(banedUserId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsersBanId other = (UsersBanId) obj;
		return Objects.equals(banedUserId, other.banedUserId) && Objects.equals(userId, other.userId);
	}

	public UsersBanId(Integer userId, Integer banedUserId) {
		this.userId = userId;
		this.banedUserId = banedUserId;
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
	
	

}
