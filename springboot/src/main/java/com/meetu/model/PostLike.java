package com.meetu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_like")
@IdClass(PostLikeId.class)
public class PostLike {

    @Id
    private Integer likeUserId;  // 保持原有的主鍵欄位

    @Id
    @Column(name = "targetType", nullable = false)
    private String targetType;  // 用於區分是 Post 還是 PostComment

    @Id
    private Integer targetId;  // 可以是 Post 或 PostComment 的 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "likeUserId", insertable = false, updatable = false)  // 維持與 Users 表的關聯
    private Users user;

    // Getters and Setters

    public Integer getLikeUserId() {
        return likeUserId;
    }

    public void setLikeUserId(Integer likeUserId) {
        this.likeUserId = likeUserId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
