package com.meetu.model.chatroom;

import java.time.Instant;
import com.meetu.model.UsersProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "chatroom_messages")
public class ChatroomMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @Column(nullable = false)
    private Integer chatroomId;

    @Column(nullable = false)
    private Integer senderId;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "chatroomId", insertable = false, updatable = false)
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "senderId", insertable = false, updatable = false)
    private UsersProfile sender;
    
    

	@Override
	public String toString() {
		return "ChatroomMessage [messageId=" + messageId + ", chatroomId=" + chatroomId + ", senderId=" + senderId
				+ ", content=" + content + ", timestamp=" + timestamp + ", chatroom=" + chatroom + ", sender=" + sender
				+ "]";
	}



	public ChatroomMessage(Integer chatroomId, Integer senderId, String content, Instant timestamp) {
		super();
		this.chatroomId = chatroomId;
		this.senderId = senderId;
		this.content = content;
		this.timestamp = timestamp;
	}

    
}