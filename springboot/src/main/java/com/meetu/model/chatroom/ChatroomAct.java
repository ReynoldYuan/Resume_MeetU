package com.meetu.model.chatroom;

import java.time.LocalDate;

import com.meetu.model.Activities;
import com.meetu.model.UsersProfile;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@Table(name = "chatroom_act")
public class ChatroomAct {
	
	@EmbeddedId
	private ChatroomActId chatroomActId;
	
	@OneToOne(fetch = FetchType.LAZY)
    @MapsId("chatroomId")
    @JoinColumn(name = "chatroomId")
	private Chatroom chatroom;
	
	
	@OneToOne(fetch = FetchType.LAZY)
    @MapsId("actId")
    @JoinColumn(name = "actId")
	private Activities activities;


	@Override
	public String toString() {
		return "ChatroomAct [chatroomActId=" + chatroomActId + ", chatroom=" + chatroom + ", activities=" + activities
				+ "]";
	}

	
}
