	package com.meetu.model;
	
	import jakarta.persistence.*;
	import java.time.LocalDateTime;
	import java.util.List;
	
	@Entity
	@Table(name = "post_comment")
	public class PostComment {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer commentId;
	
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "postId", nullable = false)
	    private Post post;
	
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "commentUserId", nullable = false)
	    private Users user;
	
	    @Column(length = 4000)
	    private String commentContent;
	
	    @Column(name = "commentCreatedAt")
	    private LocalDateTime commentCreatedAt;
	
	    @Column(name = "commentUpdatedAt")
	    private LocalDateTime commentUpdatedAt;
	
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "parentCommentId")
	    private PostComment parentComment;
	
	    @Column(name = "commentReportStatus", nullable = false)
	    private char commentReportStatus;
	
	    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    private List<PostComment> replies;
	
	    @Column(name = "replyCount", nullable = false)
	    private int replyCount;
	
	    @Column(name = "commentType", nullable = false)
	    private String commentType;
	
	    @Transient
	    private int likeCount;
	
	    // Getters and Setters
	
	    public Integer getCommentId() {
	        return commentId;
	    }
	
	    public void setCommentId(Integer commentId) {
	        this.commentId = commentId;
	    }
	
	    public Post getPost() {
	        return post;
	    }
	
	    public void setPost(Post post) {
	        this.post = post;
	    }
	
	    public Users getUser() {
	        return user;
	    }
	
	    public void setUser(Users user) {
	        this.user = user;
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
	
	    public PostComment getParentComment() {
	        return parentComment;
	    }
	
	    public void setParentComment(PostComment parentComment) {
	        this.parentComment = parentComment;
	    }
	
	    public char getCommentReportStatus() {
	        return commentReportStatus;
	    }
	
	    public void setCommentReportStatus(char commentReportStatus) {
	        this.commentReportStatus = commentReportStatus;
	    }
	
	    public List<PostComment> getReplies() {
	        return replies;
	    }
	
	    public void setReplies(List<PostComment> replies) {
	        this.replies = replies;
	    }
	
	    public int getReplyCount() {
	        return replyCount;
	    }
	
	    public void setReplyCount(int replyCount) {
	        this.replyCount = replyCount;
	    }
	
	    public void incrementReplyCount() {
	        this.replyCount++;
	    }
	
	    public void decrementReplyCount() {
	        if (this.replyCount > 0) {
	            this.replyCount--;
	        }
	    }
	
	    public String getCommentType() {
	        return commentType;
	    }
	
	    public void setCommentType(String commentType) {
	        this.commentType = commentType;
	    }
	
	    public int getLikeCount() {
	        return likeCount;
	    }
	
	    public void setLikeCount(int likeCount) {
	        this.likeCount = likeCount;
	    }
	
	    public void incrementLikeCount() {
	        this.likeCount++;
	    }
	
	    public void decrementLikeCount() {
	        if (this.likeCount > 0) {
	            this.likeCount--;
	        }
	    }
	}
