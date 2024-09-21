package com.meetu.model.chatroom;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomActId {

	private Integer chatroomId;
    private Integer actId;
    
	@Override
	public int hashCode() {
		return Objects.hash(actId, chatroomId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatroomActId other = (ChatroomActId) obj;
		return Objects.equals(actId, other.actId) && Objects.equals(chatroomId, other.chatroomId);
	}
	@Override
	public String toString() {
		return "ChatroomActId [chatroomId=" + chatroomId + ", actId=" + actId + "]";
	}
    
	
    
}
