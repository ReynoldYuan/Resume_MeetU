package com.meetu.model;

import java.io.Serializable;
import java.util.Objects;

public class PostLikeId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer likeUserId;
    private String targetType;
    private Integer targetId;

    // Default constructor
    public PostLikeId() {
    }

    public PostLikeId(Integer likeUserId, String targetType, Integer targetId) {
        this.likeUserId = likeUserId;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    // Getters, setters, equals, and hashCode
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostLikeId that = (PostLikeId) o;
        return Objects.equals(likeUserId, that.likeUserId) &&
                Objects.equals(targetType, that.targetType) &&
                Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likeUserId, targetType, targetId);
    }
}
