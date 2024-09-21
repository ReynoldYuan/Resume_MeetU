package com.meetu.model.chatroom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {

	/**
	 * MS SQL查詢語法 DECLARE @TargetChatroomId INT = 指定聊天室;
	 * 
	 * WITH CombinedUsers AS ( SELECT cd.userId, cd.chatroomId, 'In Chatroom' AS
	 * status, up.userName, up.userPics FROM chatroom_details cd JOIN users_profile
	 * up ON cd.userId = up.userId WHERE cd.chatroomId = @TargetChatroomId
	 * 
	 * UNION
	 * 
	 * SELECT cm.senderId AS userId, cm.chatroomId, 'Sent Message' AS status,
	 * up.userName, up.userPics FROM chatroom_messages cm JOIN users_profile up ON
	 * cm.senderId = up.userId WHERE cm.chatroomId = @TargetChatroomId )
	 * 
	 * SELECT DISTINCT
	 * 
	 * @TargetChatroomId AS chatroomId, cu.userId, cu.userName, cu.userPics, CASE
	 *                   WHEN MAX(CASE WHEN cu.status = 'In Chatroom' THEN 1 ELSE 0
	 *                   END) = 1 AND MAX(CASE WHEN cu.status = 'Sent Message' THEN
	 *                   1 ELSE 0 END) = 1 THEN 'In Chatroom and Sent Message' WHEN
	 *                   MAX(CASE WHEN cu.status = 'In Chatroom' THEN 1 ELSE 0 END)
	 *                   = 1 THEN 'In Chatroom but Sent No Message' ELSE 'Not In
	 *                   Chatroom but Sent Message' END AS userStatus FROM
	 *                   CombinedUsers cu GROUP BY cu.userId, cu.userName,
	 *                   cu.userPics ORDER BY cu.userId;
	 */
	@Query("SELECT NEW com.meetu.model.chatroom.ChatroomUserDTO(" +
	           ":chatroomId, " +
	           "u.userId, " +
	           "u.userName, " +
	           "u.userPics, " +
	           "CASE " +
	           "    WHEN MAX(CASE WHEN cd.id IS NOT NULL THEN 1 ELSE 0 END) = 1 AND " +
	           "         MAX(CASE WHEN cm.messageId IS NOT NULL THEN 1 ELSE 0 END) = 1 " +
	           "        THEN 'In Chatroom and Sent Message' " +
	           "    WHEN MAX(CASE WHEN cd.id IS NOT NULL THEN 1 ELSE 0 END) = 1 " +
	           "        THEN 'In Chatroom but Sent No Message' " +
	           "    ELSE 'Not In Chatroom but Sent Message' " +
	           "END) " +
	           "FROM (" +
	           "    SELECT cd.usersProfile.userId as userId, cd.chatroom.chatroomId as chatroomId " +
	           "    FROM ChatroomDetail cd " +
	           "    WHERE cd.chatroom.chatroomId = :chatroomId " +
	           "    UNION " +
	           "    SELECT cm.sender.userId as userId, cm.chatroomId as chatroomId " +
	           "    FROM ChatroomMessage cm " +
	           "    WHERE cm.chatroomId = :chatroomId" +
	           ") as combinedUsers " +
	           "JOIN UsersProfile u ON u.userId = combinedUsers.userId " +
	           "LEFT JOIN ChatroomDetail cd ON cd.usersProfile.userId = u.userId AND cd.chatroom.chatroomId = :chatroomId " +
	           "LEFT JOIN ChatroomMessage cm ON cm.sender.userId = u.userId AND cm.chatroomId = :chatroomId " +
	           "GROUP BY u.userId, u.userName, u.userPics " +
	           "ORDER BY u.userId")
	    List<ChatroomUserDTO> findChatroomUsersByRoomId(@Param("chatroomId") Integer chatroomId);
}
