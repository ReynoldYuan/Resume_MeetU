package com.meetu.model;


import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

//@AllArgsConstructor
//@NoArgsConstructor
//@Setter
//@Getter
@Entity
@Table(name = "users")
@Component
public class Users {

//  以下這句OneToMany待確認
//	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "users")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    
    private String userMail;
    
    private String userPwd;
    
	private Character vip='0';
	
	private Character userIsBan='N';
	
	private byte deleteState = 0;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "users")
    @JsonManagedReference
    private UsersProfile usersProfile;
    

//   @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "users")
//    @JsonIgnore
//   private List<UsersBan> usersBanner;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "users")
//    @JsonIgnore
//    private List<Notification> notification;
    
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "users")
//    private List<Report> report;


	
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "users")
    @JsonIgnore
	private List<Attendees> attendees;
    
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
////    @JsonIgnore
//    private List<Post> posts;
//    
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
////    @JsonIgnore
//    private List<PostComment> postComments;
//
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
////    @JsonIgnore
//    private List<PostLike> postLikes;

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", userMail=" + userMail + ", userPwd=" + userPwd + ", vip=" + vip
				+ ", userIsBan=" + userIsBan + "]";
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public Character getVip() {
		return vip;
	}

	public void setVip(Character vip) {
		this.vip = vip;
	}

	public Character getUserIsBan() {
		return userIsBan;
	}

	public void setUserIsBan(Character userIsBan) {
		this.userIsBan = userIsBan;
	}

	public byte getDeleteState() {
		return deleteState;
	}

	public void setDeleteState(byte deleteState) {
		this.deleteState = deleteState;
	}

	public UsersProfile getUsersProfile() {
		return usersProfile;
	}

	public void setUsersProfile(UsersProfile usersProfile) {
		this.usersProfile = usersProfile;
	}

//	public List<Post> getPosts() {
//		return posts;
//	}
//
//	public void setPosts(List<Post> posts) {
//		this.posts = posts;
//	}
//
//	public List<PostComment> getPostComments() {
//		return postComments;
//	}
//
//	public void setPostComments(List<PostComment> postComments) {
//		this.postComments = postComments;
//	}
//
//	public List<PostLike> getPostLikes() {
//		return postLikes;
//	}
//
//	public void setPostLikes(List<PostLike> postLikes) {
//		this.postLikes = postLikes;
//	}



}