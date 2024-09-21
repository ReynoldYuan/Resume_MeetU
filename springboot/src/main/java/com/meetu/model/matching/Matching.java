package com.meetu.model.matching;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meetu.model.UsersProfile;
import com.meetu.model.chatroom.Chatroom;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matching")
public class Matching {

    @EmbeddedId
    private MatchingId id;

    @Column(nullable = false)
    private boolean likeOrNot;

    @Column
    private LocalDate matchedDate;
    
    @Column
    private LocalDate matchedSuccessfullyDate;
    
    @Column
    private Integer chatroomId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private UsersProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userPreferId")
    @JoinColumn(name = "userPreferId")
    private UsersProfile userPrefer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroomId", insertable = false, updatable = false)
    private Chatroom chatroom;
    
	@Override
	public String toString() {
		return "Matching [id=" + id + ", likeOrNot=" + likeOrNot + ", matchedDate=" + matchedDate
				+ ", matchedSuccessfullyDate=" + matchedSuccessfullyDate + "]";
	}
    
    
    
}