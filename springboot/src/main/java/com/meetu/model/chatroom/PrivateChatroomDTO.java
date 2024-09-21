package com.meetu.model.chatroom;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChatroomDTO {
    private Integer chatroomId;
    private Character chatType;
    private Integer otherUserId;
    private String otherUserName;
    private String otherUserPics;
    private Integer actId;
    private String activitiesTitle;
    private Integer messageId;
    private String lastMessageContent;
    private Instant lastMessageTimestamp;
    private Long messageCount;
    private Long userCount;
    private Long unreadQty;
}