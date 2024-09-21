package com.meetu.model.chatroom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "chatroom")
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatroomId;

    //P為一對一私聊，G為群聊
    @Column(nullable = false, length = 1)
    private Character chatType;

    @Column(nullable = false)
    private LocalDate createDate;
    
    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatroomDetail> chatroomDetails = new ArrayList<>();
    
    // 添加一个方法来添加 ChatroomDetail
    public void addChatroomDetail(ChatroomDetail chatroomDetail) {
        chatroomDetails.add(chatroomDetail);
        chatroomDetail.setChatroom(this);
    }
    
    // 添加一个方法来移除 ChatroomDetail --> 此方法暫時不使用
    public void removeChatroomDetail(ChatroomDetail chatroomDetail) {
        chatroomDetails.remove(chatroomDetail);
        chatroomDetail.setChatroom(null);
    }

	@Override
	public String toString() {
		return "Chatroom [chatroomId=" + chatroomId + ", chatType=" + chatType + ", createDate=" + createDate
				+ ", chatroomDetails=" + chatroomDetails + "]";
	}
    
    

}