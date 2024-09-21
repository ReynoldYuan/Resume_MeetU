package com.meetu.model.chatroom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomMessageRepository extends JpaRepository<ChatroomMessage, Integer> {

	// 查找特定聊天室的最後一條消息
    ChatroomMessage findTopByChatroomIdOrderByTimestampDesc(Integer chatroomId);

	List<ChatroomMessage> findByChatroomId(Integer chatroomId);

	Long countByChatroomId(Integer chatroomId);

}
