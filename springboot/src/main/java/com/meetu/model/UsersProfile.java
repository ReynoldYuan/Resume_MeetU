package com.meetu.model;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_profile")
@Component
public class UsersProfile {

	@GenericGenerator(name = "generator" , strategy = "foreign" , parameters = @Parameter(name="property" , value = "users"))
	@Id
	@GeneratedValue(generator = "generator")
	private Integer userId;
	
	private String userName;
	
	private Character userGender;
	
	private String userPics;
	
	private String userBirth;
	
	private String userLocation;
	
	private String userJob;
	
	private String userJobPosi;
	
	private String userIntroduction;
	
	private Character userPreferAct;
	
	private Character userPreferGen;
	
	private Character userFind;
	
	private Integer userPreferAgeMax;
	
	private Integer userPreferAgeMin;
	
	private String userHobby;
	
	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	@JsonBackReference
	private Users users;
	
	public UsersProfile() {
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Character getUserGender() {
		return userGender;
	}

	public void setUserGender(Character userGender) {
		this.userGender = userGender;
	}

	public String getUserPics() {
		return userPics;
	}

	public void setUserPics(String userPics) {
		this.userPics = userPics;
	}

	public String getUserBirth() {
		return userBirth;
	}

	public void setUserBirth(String userBirth) {
		this.userBirth = userBirth;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getUserJob() {
		return userJob;
	}

	public void setUserJob(String userJob) {
		this.userJob = userJob;
	}

	public String getUserJobPosi() {
		return userJobPosi;
	}

	public void setUserJobPosi(String userJobPosi) {
		this.userJobPosi = userJobPosi;
	}

	public String getUserIntroduction() {
		return userIntroduction;
	}

	public void setUserIntroduction(String userIntroduction) {
		this.userIntroduction = userIntroduction;
	}

	public Character getUserPreferAct() {
		return userPreferAct;
	}

	public void setUserPreferAct(Character userPreferAct) {
		this.userPreferAct = userPreferAct;
	}

	public Character getUserPreferGen() {
		return userPreferGen;
	}

	public void setUserPreferGen(Character userPreferGen) {
		this.userPreferGen = userPreferGen;
	}

	public Character getUserFind() {
		return userFind;
	}

	public void setUserFind(Character userFind) {
		this.userFind = userFind;
	}

	public Integer getUserPreferAgeMax() {
		return userPreferAgeMax;
	}

	public void setUserPreferAgeMax(Integer userPreferAgeMax) {
		this.userPreferAgeMax = userPreferAgeMax;
	}

	public Integer getUserPreferAgeMin() {
		return userPreferAgeMin;
	}

	public void setUserPreferAgeMin(Integer userPreferAgeMin) {
		this.userPreferAgeMin = userPreferAgeMin;
	}

	public String getUserHobby() {
		return userHobby;
	}

	public void setUserHobby(String userHobby) {
		this.userHobby = userHobby;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "userId=" + userId + ", userName=" + userName + ", userGender=" + userGender
				+ ", userPics=" + userPics + ", userBirth=" + userBirth + ", userLocation=" + userLocation
				+ ", userJob=" + userJob + ", userJobPosi=" + userJobPosi + ", userIntroduction=" + userIntroduction
				+ ", userPreferAct=" + userPreferAct + ", userPreferGen=" + userPreferGen + ", userFind=" + userFind
				+ ", userPreferAgeMax=" + userPreferAgeMax + ", userPreferAgeMin=" + userPreferAgeMin + ", userHobby="
				+ userHobby;
	}
	
	

}
