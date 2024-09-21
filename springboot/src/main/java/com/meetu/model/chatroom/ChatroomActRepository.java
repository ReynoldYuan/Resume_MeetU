package com.meetu.model.chatroom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatroomActRepository extends JpaRepository<ChatroomAct, ChatroomActId> {

	   @Query("SELECT ca.chatroom FROM ChatroomAct ca WHERE ca.activities.activitiesId = :activitiesId")
	    Optional<Chatroom> findChatroomByActivitiesId(@Param("activitiesId") Integer activitiesId);
}
