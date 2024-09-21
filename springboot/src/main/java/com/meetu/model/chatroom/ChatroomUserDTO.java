package com.meetu.model.chatroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomUserDTO {
    private Integer chatroomId;
    private Integer userId;
    private String userName;
    private String userPics;
    private String userStatus;
}