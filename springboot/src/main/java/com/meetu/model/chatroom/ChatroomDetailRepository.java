package com.meetu.model.chatroom;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.meetu.model.UsersProfile;

public interface ChatroomDetailRepository extends JpaRepository<ChatroomDetail, ChatroomDetailId> {

	@Query("SELECT new map(c.chatroomId as chatroomId, cd.id.userId as otherUserId) " + "FROM ChatroomDetail cd "
			+ "JOIN cd.chatroom c " + "WHERE c.chatType = 'P' " + "AND cd.id.chatroomId IN ("
			+ "    SELECT cd2.id.chatroomId " + "    FROM ChatroomDetail cd2 " + "    WHERE cd2.id.userId = :userId"
			+ ") " + "AND cd.id.userId != :userId")
	List<Map<String, Integer>> findPrivateChatRoomsAndOtherUserIds(@Param("userId") Integer userId);

	/**
	 * 對應的MS SQL語法 DECLARE @userId INT; SET @userId = 指定id; WITH MessageCounts AS (
	 * SELECT chatroomId, COUNT(*) AS messageCount FROM chatroom_messages GROUP BY
	 * chatroomId ), LatestMessages AS ( SELECT m.chatroomId, m.messageId,
	 * m.content, m.timestamp FROM chatroom_messages m INNER JOIN ( SELECT
	 * chatroomId, MAX(timestamp) AS maxTimestamp FROM chatroom_messages GROUP BY
	 * chatroomId ) lm ON m.chatroomId = lm.chatroomId AND m.timestamp =
	 * lm.maxTimestamp ), OtherUser AS ( SELECT cd.chatroomId, (SELECT TOP 1
	 * cd2.userId FROM chatroom_details cd2 WHERE cd2.chatroomId = cd.chatroomId AND
	 * cd2.userId != @userId) AS otherUserId FROM chatroom_details cd WHERE
	 * cd.userId = @userId ) SELECT c.chatroomId, c.chatType, ou.otherUserId,
	 * up.userName AS otherUserName, up.userPics AS otherUserPics, ca.actId,
	 * a.activitiesTitle, lm.messageId, lm.content AS lastMessageContent,
	 * lm.timestamp AS lastMessageTimestamp, ISNULL(mc.messageCount, 0) AS
	 * messageCount, 2 AS userCount, ISNULL(mc.messageCount, 0) - ISNULL(cd.readQty,
	 * 0) AS unreadQty FROM chatroom_details cd INNER JOIN chatroom c ON
	 * cd.chatroomId = c.chatroomId INNER JOIN OtherUser ou ON c.chatroomId =
	 * ou.chatroomId LEFT JOIN users_profile up ON ou.otherUserId = up.userId LEFT
	 * JOIN chatroom_act ca ON c.chatroomId = ca.chatroomId LEFT JOIN activities a
	 * ON ca.actId = a.activitiesId LEFT JOIN LatestMessages lm ON c.chatroomId =
	 * lm.chatroomId LEFT JOIN MessageCounts mc ON c.chatroomId = mc.chatroomId
	 * WHERE cd.userId = @userId AND c.chatType = 'P' ORDER BY ISNULL(lm.timestamp,
	 * '1900-01-01') DESC
	 */
	@Query(nativeQuery = true, value = "WITH MessageCounts AS ( " + "    SELECT chatroomId, COUNT(*) AS messageCount "
			+ "    FROM chatroom_messages " + "    GROUP BY chatroomId " + "), " + "LatestMessages AS ( "
			+ "    SELECT m.chatroomId, m.messageId, m.content, m.timestamp " + "    FROM chatroom_messages m "
			+ "    INNER JOIN ( " + "        SELECT chatroomId, MAX(timestamp) AS maxTimestamp "
			+ "        FROM chatroom_messages " + "        GROUP BY chatroomId "
			+ "    ) lm ON m.chatroomId = lm.chatroomId AND m.timestamp = lm.maxTimestamp " + "), " + "OtherUser AS ( "
			+ "    SELECT cd.chatroomId, " + "           (SELECT TOP 1 cd2.userId "
			+ "            FROM chatroom_details cd2 "
			+ "            WHERE cd2.chatroomId = cd.chatroomId AND cd2.userId != :userId) AS otherUserId "
			+ "    FROM chatroom_details cd " + "    WHERE cd.userId = :userId " + ") " + "SELECT "
			+ "    c.chatroomId, " + "    c.chatType, " + "    ou.otherUserId, " + "    up.userName AS otherUserName, "
			+ "    up.userPics AS otherUserPics, " + "    ca.actId, " + "    a.activitiesTitle, " + "    lm.messageId, "
			+ "    lm.content AS lastMessageContent, " + "    lm.timestamp AS lastMessageTimestamp, "
			+ "    ISNULL(mc.messageCount, 0) AS messageCount, " + "    2 AS userCount, "
			+ "    ISNULL(mc.messageCount, 0) - ISNULL(cd.readQty, 0) AS unreadQty " + "FROM "
			+ "    chatroom_details cd " + "INNER JOIN " + "    chatroom c ON cd.chatroomId = c.chatroomId "
			+ "INNER JOIN " + "    OtherUser ou ON c.chatroomId = ou.chatroomId " + "LEFT JOIN "
			+ "    users_profile up ON ou.otherUserId = up.userId " + "LEFT JOIN "
			+ "    chatroom_act ca ON c.chatroomId = ca.chatroomId " + "LEFT JOIN "
			+ "    activities a ON ca.actId = a.activitiesId " + "LEFT JOIN "
			+ "    LatestMessages lm ON c.chatroomId = lm.chatroomId " + "LEFT JOIN "
			+ "    MessageCounts mc ON c.chatroomId = mc.chatroomId " + "WHERE "
			+ "    cd.userId = :userId AND c.chatType = 'P' " + "ORDER BY "
			+ "    ISNULL(lm.timestamp, '1900-01-01') DESC")
	List<Object[]> findPrivateChatroomsByUserId(@Param("userId") Integer userId);

	/**
	 * 對應的MS SQL語法 DECLARE @userId INT; SET @userId = 指定id; DECLARE @userId INT;
	 * SET @userId = 指定的用戶ID ;
	 * 
	 * WITH MessageCounts AS ( SELECT chatroomId, COUNT(*) AS messageCount FROM
	 * chatroom_messages GROUP BY chatroomId ), UserCounts AS ( SELECT chatroomId,
	 * COUNT(DISTINCT userId) AS userCount FROM chatroom_details GROUP BY chatroomId
	 * ), LatestMessages AS ( SELECT m.chatroomId, m.messageId, m.content,
	 * m.timestamp FROM chatroom_messages m INNER JOIN ( SELECT chatroomId,
	 * MAX(timestamp) AS maxTimestamp FROM chatroom_messages GROUP BY chatroomId )
	 * lm ON m.chatroomId = lm.chatroomId AND m.timestamp = lm.maxTimestamp ) SELECT
	 * c.chatroomId, c.chatType, ca.actId, a.activitiesTitle, lm.messageId,
	 * lm.content AS lastMessageContent, lm.timestamp AS lastMessageTimestamp,
	 * ISNULL(mc.messageCount, 0) AS messageCount, ISNULL(uc.userCount, 0) AS
	 * userCount, ISNULL(mc.messageCount, 0) - ISNULL(cd.readQty, 0) AS unreadQty
	 * FROM chatroom_details cd INNER JOIN chatroom c ON cd.chatroomId =
	 * c.chatroomId LEFT JOIN chatroom_act ca ON c.chatroomId = ca.chatroomId LEFT
	 * JOIN activities a ON ca.actId = a.activitiesId LEFT JOIN LatestMessages lm ON
	 * c.chatroomId = lm.chatroomId LEFT JOIN MessageCounts mc ON c.chatroomId =
	 * mc.chatroomId LEFT JOIN UserCounts uc ON c.chatroomId = uc.chatroomId WHERE
	 * cd.userId = @userId AND c.chatType = 'G' AND a.activitiesReportStatus = 'N'
	 * ORDER BY ISNULL(lm.timestamp, '1900-01-01') DESC;
	 */
	@Query(nativeQuery = true, value = "WITH MessageCounts AS ( " + "    SELECT chatroomId, COUNT(*) AS messageCount "
			+ "    FROM chatroom_messages " + "    GROUP BY chatroomId " + "), " + "UserCounts AS ( "
			+ "    SELECT chatroomId, COUNT(DISTINCT userId) AS userCount " + "    FROM chatroom_details "
			+ "    GROUP BY chatroomId " + "), " + "LatestMessages AS ( "
			+ "    SELECT m.chatroomId, m.messageId, m.content, m.timestamp " + "    FROM chatroom_messages m "
			+ "    INNER JOIN ( " + "        SELECT chatroomId, MAX(timestamp) AS maxTimestamp "
			+ "        FROM chatroom_messages " + "        GROUP BY chatroomId "
			+ "    ) lm ON m.chatroomId = lm.chatroomId AND m.timestamp = lm.maxTimestamp " + ") " + "SELECT "
			+ "    c.chatroomId, " + "    c.chatType, " + "    ca.actId, " + "    a.activitiesTitle, "
			+ "    lm.messageId, " + "    lm.content AS lastMessageContent, "
			+ "    lm.timestamp AS lastMessageTimestamp, " + "    ISNULL(mc.messageCount, 0) AS messageCount, "
			+ "    ISNULL(uc.userCount, 0) AS userCount, "
			+ "    ISNULL(mc.messageCount, 0) - ISNULL(cd.readQty, 0) AS unreadQty " + "FROM "
			+ "    chatroom_details cd " + "INNER JOIN " + "    chatroom c ON cd.chatroomId = c.chatroomId "
			+ "LEFT JOIN " + "    chatroom_act ca ON c.chatroomId = ca.chatroomId " + "LEFT JOIN "
			+ "    activities a ON ca.actId = a.activitiesId " + "LEFT JOIN "
			+ "    LatestMessages lm ON c.chatroomId = lm.chatroomId " + "LEFT JOIN "
			+ "    MessageCounts mc ON c.chatroomId = mc.chatroomId " + "LEFT JOIN "
			+ "    UserCounts uc ON c.chatroomId = uc.chatroomId " + "WHERE "
			+ "    cd.userId = :userId AND c.chatType = 'G' " 
			+ " AND a.activitiesReportStatus = 'N'"
			+ "ORDER BY "
			+ "    ISNULL(lm.timestamp, '1900-01-01') DESC")
	List<Object[]> findActChatroomsByUserId(@Param("userId") Integer userId);

	@Query("SELECT cd.id.userId FROM ChatroomDetail cd WHERE cd.id.chatroomId = :chatroomId")
	List<Integer> findUserIdsByChatroomId(@Param("chatroomId") Integer chatroomId);

	@Query("SELECT cd.usersProfile FROM ChatroomDetail cd WHERE cd.id.chatroomId = :chatroomId")
	List<UsersProfile> findUsersProfilesByChatroomId(@Param("chatroomId") Integer chatroomId);

}
