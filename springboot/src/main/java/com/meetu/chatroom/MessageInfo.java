package com.meetu.chatroom;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInfo {
	private Integer chatroomId;
	private Integer senderId;
    private String type;
    private String content;
    private Instant timestamp;
    // 添加其他需要的字段
  
}