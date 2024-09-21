package com.meetu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "activities_collect")
public class ActivitiesCollect {
	
	@EmbeddedId
	private ActivitiesCollectId actCollectId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("collectUserId")
	@JoinColumn(name = "collectUserId")
	@JsonIgnore
	private Users users;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("collectActId")
	@JoinColumn(name = "collectActId")
	@JsonIgnore
	private Activities activities;
	

	public ActivitiesCollect() {
	}

	public ActivitiesCollectId getActCollectId() {
		return actCollectId;
	}

	public void setActCollectId(ActivitiesCollectId actCollectId) {
		this.actCollectId = actCollectId;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Activities getActivities() {
		return activities;
	}

	public void setActivities(Activities activities) {
		this.activities = activities;
	}

}
