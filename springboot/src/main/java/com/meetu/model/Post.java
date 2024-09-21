package com.meetu.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    private Integer postUserId;

    @ManyToOne
    @JoinColumn(name = "postUserId", insertable = false, updatable = false)
    private Users user;  // 與 Users 的關聯

    @Column(length = 4000)
    private String caption;

    private String imageUrl;

    private String videoUrl;

    @Column(name = "postCreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime postCreatedAt;

    @Column(name = "postUpdatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime postUpdatedAt;

    @Column(name = "postReportStatus", columnDefinition = "CHAR(1) DEFAULT 'N'", nullable = false)
    private char postReportStatus;

    @Column(name = "postType", columnDefinition = "VARCHAR(255) DEFAULT 'POST'", nullable = false)
    private String postType;  // 用於區分不同的 post 類型

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> comments;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<PostLike> likes;

    // 動態計算愛心數量的屬性
    @Transient
    private int likeCount;
    
    // 新增的屬性：留言數量，不儲存於資料庫
    @Transient
    private int commentCount;

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getPostUserId() {
		return postUserId;
	}

	public void setPostUserId(Integer postUserId) {
		this.postUserId = postUserId;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public LocalDateTime getPostCreatedAt() {
		return postCreatedAt;
	}

	public void setPostCreatedAt(LocalDateTime postCreatedAt) {
		this.postCreatedAt = postCreatedAt;
	}

	public LocalDateTime getPostUpdatedAt() {
		return postUpdatedAt;
	}

	public void setPostUpdatedAt(LocalDateTime postUpdatedAt) {
		this.postUpdatedAt = postUpdatedAt;
	}

	public char getPostReportStatus() {
		return postReportStatus;
	}

	public void setPostReportStatus(char postReportStatus) {
		this.postReportStatus = postReportStatus;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public List<PostComment> getComments() {
		return comments;
	}

	public void setComments(List<PostComment> comments) {
		this.comments = comments;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

}
