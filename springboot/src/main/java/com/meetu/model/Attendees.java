package com.meetu.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "attendees")
public class Attendees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entryId")
    private Integer entryId;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendeeId")
	@JsonIgnore
    private Users users;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activityId")
    @JsonIgnore
    private Activities activities;

    private boolean isApproved = false;

    private boolean isCompleted = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = new Date();
    }

    // Getters and Setters
    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
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

	public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }
}
