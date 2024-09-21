package com.meetu.model.chatroom;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meetu.model.UsersProfile;

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

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "chatroom_details")
public class ChatroomDetail {
	@EmbeddedId
    private ChatroomDetailId id;

    @Column(nullable = false)
    private LocalDate joinDate;
    
    @Column
    private Long readQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatroomId")
    @JoinColumn(name = "chatroomId")
    private Chatroom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private UsersProfile usersProfile;
    
 // 添加一个方法来设置关系和ID
    public void setRelationship(Chatroom chatroom, UsersProfile usersProfile) {
        this.chatroom = chatroom;
        this.usersProfile = usersProfile;
        this.id = new ChatroomDetailId(chatroom.getChatroomId(), usersProfile.getUserId());
    }

	@Override
	public String toString() {
		return "ChatroomDetail [id=" + id + ", joinDate=" + joinDate + "]";
	}

    
}
    