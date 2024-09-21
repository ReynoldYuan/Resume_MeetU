package com.meetu.model;

import java.time.LocalDateTime;

public class PostCommentDTO {

    private Integer commentId;
    private Integer postId; // Post 的 ID
    private Integer userId; // User 的 ID
    private String userName; // 使用者名稱
    private String userPics; // 使用者頭像
    private String commentContent;
    private LocalDateTime commentCreatedAt;
    private LocalDateTime commentUpdatedAt;
    private char commentReportStatus;
    private String commentType;
    private long likeCount;  // 使用 long 類型表示點贊數

    // Constructors
    public PostCommentDTO() {
    }

    public PostCommentDTO(Integer commentId, Integer postId, Integer userId, String userName, String userPics,
                          String commentContent, LocalDateTime commentCreatedAt, LocalDateTime commentUpdatedAt,
                          Character commentReportStatus, String commentType, long likeCount) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.userPics = userPics;
        this.commentContent = commentContent;
        this.commentCreatedAt = commentCreatedAt;
        this.commentUpdatedAt = commentUpdatedAt;
        this.commentReportStatus = commentReportStatus;
        this.commentType = commentType;
        this.likeCount = likeCount;
    }

    // 使用 PostComment 對象初始化 DTO 屬性
    public PostCommentDTO(PostComment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.userId = comment.getUser().getUserId();
        this.userName = comment.getUser().getUsersProfile().getUserName();
        this.userPics = comment.getUser().getUsersProfile().getUserPics();
        this.commentContent = comment.getCommentContent();
        this.commentCreatedAt = comment.getCommentCreatedAt();
        this.commentUpdatedAt = comment.getCommentUpdatedAt();
        this.commentReportStatus = comment.getCommentReportStatus();
        this.commentType = comment.getCommentType();
        this.likeCount = comment.getLikeCount(); // 預設為 0 或使用 comment.getLikeCount() 獲取
    }

    // Getters and Setters

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
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

    public String getUserPics() {
        return userPics;
    }

    public void setUserPics(String userPics) {
        this.userPics = userPics;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public LocalDateTime getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public void setCommentCreatedAt(LocalDateTime commentCreatedAt) {
        this.commentCreatedAt = commentCreatedAt;
    }

    public LocalDateTime getCommentUpdatedAt() {
        return commentUpdatedAt;
    }

    public void setCommentUpdatedAt(LocalDateTime commentUpdatedAt) {
        this.commentUpdatedAt = commentUpdatedAt;
    }

    public char getCommentReportStatus() {
        return commentReportStatus;
    }

    public void setCommentReportStatus(char commentReportStatus) {
        this.commentReportStatus = commentReportStatus;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
    @Override
    public String toString() {
        return "PostCommentDTO{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPics='" + userPics + '\'' +
                ", commentContent='" + commentContent + '\'' +
                ", commentCreatedAt=" + commentCreatedAt +
                ", commentUpdatedAt=" + commentUpdatedAt +
                ", commentReportStatus=" + commentReportStatus +
                ", commentType='" + commentType + '\'' +
                ", likeCount=" + likeCount +
                '}';
    }
}
