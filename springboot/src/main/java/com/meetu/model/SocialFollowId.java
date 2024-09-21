package com.meetu.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class SocialFollowId implements Serializable {

    private Integer followerId;
    private Integer followeeId;

    public SocialFollowId() {}

    public SocialFollowId(Integer followerId, Integer followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public Integer getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Integer followerId) {
        this.followerId = followerId;
    }

    public Integer getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(Integer followeeId) {
        this.followeeId = followeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(followeeId, followerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SocialFollowId other = (SocialFollowId) obj;
        return Objects.equals(followeeId, other.followeeId) && Objects.equals(followerId, other.followerId);
    }
}