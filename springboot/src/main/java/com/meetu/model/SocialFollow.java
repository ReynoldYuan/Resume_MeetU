package com.meetu.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "social_follow")
public class SocialFollow {

    @EmbeddedId
    private SocialFollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "followerId")
    private UsersProfile follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followeeId")
    @JoinColumn(name = "followeeId")
    private UsersProfile followee;

    public SocialFollowId getId() {
        return id;
    }

    public void setId(SocialFollowId id) {
        this.id = id;
    }

    public UsersProfile getFollower() {
        return follower;
    }

    public void setFollower(UsersProfile follower) {
        this.follower = follower;
    }

    public UsersProfile getFollowee() {
        return followee;
    }

    public void setFollowee(UsersProfile followee) {
        this.followee = followee;
    }
}