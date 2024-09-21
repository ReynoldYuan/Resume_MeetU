package com.meetu.model;

import java.time.LocalDateTime;

public class PostDTO {

    private Integer postId;
    private String caption;
    private String imageUrl;
    private String videoUrl;
    private LocalDateTime postCreatedAt;
    private LocalDateTime postUpdatedAt;
    private long likeCount;
    private long commentCount;
    private char postReportStatus;

    // 新增的屬性：用戶ID
    private Integer userId;  
    private String userName;
    private String userPics;

    // Constructor
    public PostDTO(Integer postId, String caption, String imageUrl, String videoUrl, LocalDateTime postCreatedAt,
                   LocalDateTime postUpdatedAt, long likeCount, long commentCount, char postReportStatus, 
                   Integer userId, String userName, String userPics) {
        this.postId = postId;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.postCreatedAt = postCreatedAt;
        this.postUpdatedAt = postUpdatedAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postReportStatus = postReportStatus;
        this.userId = userId;
        this.userName = userName;
        this.userPics = userPics;
    }

    // Getters and Setters
    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
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

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public char getPostReportStatus() {
        return postReportStatus;
    }

    public void setPostReportStatus(char postReportStatus) {
        this.postReportStatus = postReportStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPics() {
        return userPics;
    }

    public void setUserPics(String userPics) {
        this.userPics = userPics;
    }
    
 // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "postId=" + postId +
                ", caption='" + caption + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", postCreatedAt=" + postCreatedAt +
                ", postUpdatedAt=" + postUpdatedAt +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", postReportStatus=" + postReportStatus +
                ", userName='" + userName + '\'' +
                ", userPics='" + userPics + '\'' +
                '}';
    }
}
