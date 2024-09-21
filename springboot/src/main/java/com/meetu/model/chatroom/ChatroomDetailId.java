package com.meetu.model.chatroom;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ChatroomDetailId implements Serializable {
    private Integer chatroomId;
    private Integer userId;

    public ChatroomDetailId() {}

    public ChatroomDetailId(Integer chatroomId, Integer userId) {
        this.chatroomId = chatroomId;
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatroomDetailId that = (ChatroomDetailId) o;
        return Objects.equals(chatroomId, that.chatroomId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatroomId, userId);
    }

	@Override
	public String toString() {
		return "ChatroomDetailId [chatroomId=" + chatroomId + ", userId=" + userId + "]";
	}
    
    
}