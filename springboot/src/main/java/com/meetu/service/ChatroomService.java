package com.meetu.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.meetu.model.Activities;
import com.meetu.model.UsersProfile;
import com.meetu.model.chatroom.ActChatroomDTO;
import com.meetu.model.chatroom.Chatroom;
import com.meetu.model.chatroom.ChatroomAct;
import com.meetu.model.chatroom.ChatroomActId;
import com.meetu.model.chatroom.ChatroomActRepository;
import com.meetu.model.chatroom.ChatroomDetail;
import com.meetu.model.chatroom.ChatroomDetailId;
import com.meetu.model.chatroom.ChatroomDetailRepository;
import com.meetu.model.chatroom.ChatroomMessage;
import com.meetu.model.chatroom.ChatroomMessageRepository;
import com.meetu.model.chatroom.ChatroomRepository;
import com.meetu.model.chatroom.ChatroomUserDTO;
import com.meetu.model.chatroom.PrivateChatroomDTO;

@Service
@Transactional
public class ChatroomService {

	@Autowired
	public ChatroomRepository cRep;

	@Autowired
	public ChatroomDetailRepository cdRep;

	@Autowired
	public ChatroomMessageRepository cmRep;

	@Autowired
	public ChatroomActRepository caRep;

	@Autowired
	public UsersProfileService upService;

	@Value("${domain.url}")
	private String domainUrl;

	// 找到使用者的一對一聊天室id和對方id 已作廢不用
	public List<Map<String, Object>> findUserPersonalChatroom(Integer loginId) {
		// 獲取原始的聊天室和其他用戶ID數據
		List<Map<String, Integer>> originalData = cdRep.findPrivateChatRoomsAndOtherUserIds(loginId);

		// 創建一個新的List來存儲增強後的數據
		List<Map<String, Object>> enhancedData = new ArrayList<>();

		// 使用foreach循環處理每個Map
		for (Map<String, Integer> chatroom : originalData) {
			// 創建一個新的Map來存儲增強的數據
			Map<String, Object> enhancedChatroom = new HashMap<>();

			// 複製原始數據
			enhancedChatroom.put("chatroomId", chatroom.get("chatroomId"));
			enhancedChatroom.put("otherUserId", chatroom.get("otherUserId"));

			// 獲取其他用戶的資料
			Integer otherUserId = chatroom.get("otherUserId");
			UsersProfile otherUserProfile = upService.findByUserId(otherUserId);
			if (otherUserProfile != null) {
				enhancedChatroom.put("otherUserPics", domainUrl + otherUserProfile.getUserPics());
				enhancedChatroom.put("otherUserName", otherUserProfile.getUserName());
			}

			// 獲取最後一條聊天消息
			Integer chatroomId = chatroom.get("chatroomId");
			ChatroomMessage lastMessage = cmRep.findTopByChatroomIdOrderByTimestampDesc(chatroomId);
			if (lastMessage != null) {
				enhancedChatroom.put("lastMessageContent", lastMessage.getContent());
				enhancedChatroom.put("lastMessageTimestamp", lastMessage.getTimestamp());
			} else {
				enhancedChatroom.put("lastMessageContent", null);
				enhancedChatroom.put("lastMessageTimestamp", null);
			}

			// 將增強後的Map添加到新的List中
			enhancedData.add(enhancedChatroom);
		}

		return enhancedData;
	}

	public ChatroomDetail readAllMessage(Integer chatroomId, Integer loginId) {
		try {
			Long totalMsgQty = cmRep.countByChatroomId(chatroomId);
			if (totalMsgQty == null || totalMsgQty == 0L) {
				//不執行任何動作
			} else {				
				Optional<ChatroomDetail> cd = cdRep.findById(new ChatroomDetailId(chatroomId, loginId));
				if (cd.isPresent()) {
					ChatroomDetail chatroomDetail = cd.get();
					Long readQty = chatroomDetail.getReadQty()==null?0L:chatroomDetail.getReadQty();
					if(totalMsgQty==readQty) {
						System.out.println("不用儲存:"+chatroomDetail);
						return chatroomDetail;
					} else {
						System.out.println("儲存前:"+chatroomDetail);
						chatroomDetail.setReadQty(totalMsgQty);
						ChatroomDetail save = cdRep.save(chatroomDetail);
						System.out.println("儲存後:"+save);
						return save;						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public List<ChatroomMessage> getChatHistory(Integer chatroomId) {
		return cmRep.findByChatroomId(chatroomId);
	}

	public void saveMessages(ChatroomMessage userMessage) {
		try {
			cmRep.save(userMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public List<UsersProfile> getUsersProfilesForChatroom(Integer chatroomId) {
        return cdRep.findUsersProfilesByChatroomId(chatroomId);
    }
	
	public Chatroom createUserPersonalChatroom(List<UsersProfile> matchedUsers) {
		try {
			Chatroom chatroom = new Chatroom();
			chatroom.setChatType('P');
			chatroom.setCreateDate(LocalDate.now());
			chatroom = cRep.save(chatroom);

			// 為配對成功的雙方創建 ChatroomDetail
			for (UsersProfile user : matchedUsers) {
				ChatroomDetail detail = new ChatroomDetail();
				detail.setJoinDate(LocalDate.now());

				// 使用新添加的方法来设置关系和ID
				detail.setRelationship(chatroom, user);

				// 使用我们在 Chatroom 类中添加的方法来设置关系
				chatroom.addChatroomDetail(detail);
			}

			// 再次保存 Chatroom 以保存所有的 ChatroomDetail
			Chatroom savedChatroom = cRep.save(chatroom);

			return savedChatroom;
		} catch (Exception e) {
			System.out.println("創建聊天室時出錯了!!");
		}
		return null;

	}

	public ChatroomAct createActChatroom(Activities act) {
		if (act != null) {
			try {
				Chatroom chatroom = new Chatroom();
				chatroom.setChatType('G');
				chatroom.setCreateDate(LocalDate.now());
				Chatroom savedChatroom = cRep.save(chatroom);
				System.out.println("savedChatroom:" + savedChatroom);

				ChatroomAct chatroomAct = new ChatroomAct();
				chatroomAct.setChatroomActId(new ChatroomActId(savedChatroom.getChatroomId(), act.getActivitiesId()));
				chatroomAct.setActivities(act);
				chatroomAct.setChatroom(savedChatroom);
				ChatroomAct save = caRep.save(chatroomAct);
				System.out.println("chatroomAct:" + save);

				UsersProfile usersProfile = new UsersProfile();
				usersProfile.setUserId(act.getUsers().getUserId());
				ChatroomDetail detail = new ChatroomDetail();
				detail.setJoinDate(LocalDate.now());

				// 使用新添加的方法来设置关系和ID
				detail.setRelationship(chatroom, usersProfile);

				// 使用我们在 Chatroom 类中添加的方法来设置关系
				savedChatroom.addChatroomDetail(detail);

				// 再次保存 Chatroom 以保存所有的 ChatroomDetail
				cRep.save(savedChatroom);

				return chatroomAct;
			} catch (Exception e) {
				System.out.println("活動聊天室沒存成功");
				e.printStackTrace();
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
		}
		return null;
	}

	/**
	 * @param actId                為活動Id
	 * @param approvedId為被審核通過的參加者
	 * @return 對應的聊天室詳情
	 */
	public ChatroomDetail addIntoChatroom(Integer actId, Integer approvedId) {
		try {
			Optional<Chatroom> chatroom = caRep.findChatroomByActivitiesId(actId);
			if (chatroom.isPresent()) {
				ChatroomDetailId chatroomDetailId = new ChatroomDetailId(chatroom.get().getChatroomId(), approvedId);
				ChatroomDetail chatroomDetail = new ChatroomDetail();
				chatroomDetail.setId(chatroomDetailId);
				chatroomDetail.setJoinDate(LocalDate.now());
				UsersProfile usersProfile = new UsersProfile();
				usersProfile.setUserId(approvedId);
				chatroomDetail.setRelationship(chatroom.get(), usersProfile);
				ChatroomDetail save = cdRep.save(chatroomDetail);
				if (save.getId().getChatroomId() == chatroom.get().getChatroomId()) {
					return save;
				}
			} else {
				System.out.println("儲存聊天參與者出錯");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<PrivateChatroomDTO> getPrivateChatroomsForUser(Integer userId) {
		List<Object[]> rawData = cdRep.findPrivateChatroomsByUserId(userId);
		return rawData.stream().map(this::convertToPrivateChatroomDTO).collect(Collectors.toList());
	}

	public List<ActChatroomDTO> getActChatroomsForUser(Integer userId) {
		List<Object[]> rawData = cdRep.findActChatroomsByUserId(userId);
		return rawData.stream().map(this::convertToActChatroomDTO).collect(Collectors.toList());
	}

	private PrivateChatroomDTO convertToPrivateChatroomDTO(Object[] row) {
		String userPics = (String) row[4]; // 假設其他使用者的圖片在第5個位置
		String fullUserPicsUrl = userPics != null ? domainUrl + userPics : null;

		return new PrivateChatroomDTO((Integer) row[0], // chatroomId
				(Character) row[1], // chatType
				(Integer) row[2], // otherUserId
				(String) row[3], // otherUserName
				fullUserPicsUrl, // otherUserPics
				(Integer) row[5], // actId
				(String) row[6], // activitiesTitle
				(Integer) row[7], // messageId
				(String) row[8], // lastMessageContent
				convertToInstant(row[9]), // lastMessageTimestamp
				convertToLong(row[10]), // messageCount
				convertToLong(row[11]), // userCount
				convertToLong(row[12]) // unreadQty
		);
	}

	private ActChatroomDTO convertToActChatroomDTO(Object[] row) {
		return new ActChatroomDTO((Integer) row[0], // chatroomId
				(Character) row[1], // chatType
				(Integer) row[2], // actId
				(String) row[3], // activitiesTitle
				(Integer) row[4], // messageId
				(String) row[5], // lastMessageContent
				convertToInstant(row[6]), // lastMessageTimestamp
				convertToLong(row[7]), // messageCount
				convertToLong(row[8]), // userCount
				convertToLong(row[9]) // unreadQty
		);
	}

	private Instant convertToInstant(Object dateObject) {
		if (dateObject == null) {
			return null;
		}
		if (dateObject instanceof Timestamp) {
			return ((Timestamp) dateObject).toInstant();
		}
		throw new IllegalArgumentException("Unsupported date type: " + dateObject.getClass());
	}

	private Long convertToLong(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		throw new IllegalArgumentException("Unsupported number type: " + value.getClass());
	}

	public List<ChatroomUserDTO> getChatroomUsersByRoomId(Integer chatroomId) {
		List<ChatroomUserDTO> users = cRep.findChatroomUsersByRoomId(chatroomId);
		return users.stream().map(this::addDomainUrlToUserPics).collect(Collectors.toList());
	}

	private ChatroomUserDTO addDomainUrlToUserPics(ChatroomUserDTO user) {
		if (user.getUserPics() != null && !user.getUserPics().isEmpty()) {
			user.setUserPics(domainUrl + user.getUserPics());
		}
		return user;
	}

	public List<Integer> getUsersInChatroom(Integer chatroomId) {
		try {
			List<Integer> userIdsByChatroomId = cdRep.findUserIdsByChatroomId(chatroomId);
			return userIdsByChatroomId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
