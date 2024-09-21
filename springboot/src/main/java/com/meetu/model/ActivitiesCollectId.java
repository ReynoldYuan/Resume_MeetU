package com.meetu.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class ActivitiesCollectId implements Serializable{
	
	private Integer collectUserId;
	private Integer collectActId;
	

	public ActivitiesCollectId() {
	}


	public ActivitiesCollectId(Integer collectUserId, Integer collectActId) {
		this.collectUserId = collectUserId;
		this.collectActId = collectActId;
	}


	public Integer getCollectUserId() {
		return collectUserId;
	}


	public void setCollectUserId(Integer collectUserId) {
		this.collectUserId = collectUserId;
	}


	public Integer getCollectActId() {
		return collectActId;
	}


	public void setCollectActId(Integer collectActId) {
		this.collectActId = collectActId;
	}


	@Override
	public int hashCode() {
		return Objects.hash(collectActId, collectUserId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivitiesCollectId other = (ActivitiesCollectId) obj;
		return Objects.equals(collectActId, other.collectActId) && Objects.equals(collectUserId, other.collectUserId);
	}

}
