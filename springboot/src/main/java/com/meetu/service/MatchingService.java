package com.meetu.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.meetu.model.UsersProfile;
import com.meetu.model.UsersProfileRespository;
import com.meetu.model.chatroom.Chatroom;
import com.meetu.model.matching.Matching;
import com.meetu.model.matching.MatchingId;
import com.meetu.model.matching.MatchingRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MatchingService {

	@Autowired
	public UsersProfileRespository upRep;

	@Autowired
	public MatchingRepository mRep;

	@Autowired
	public ChatroomService cService;

	/**
	 * 儲存時會同步檢查是否配對成功，配對成功則雙方建立聊天室
	 */
	public Matching saveMatchingResult(Matching matching) {
		try {
			MatchingId id = matching.getId();
			Optional<Matching> loginOptional = mRep.findById(id);
			UsersProfile loginProfile = new UsersProfile();
			UsersProfile loginPrefer = new UsersProfile();
			// 先取得配對聊天室的資料，避免重複創聊天室
			if (loginOptional.isPresent()) {
				matching.setChatroom(loginOptional.get().getChatroom());
				matching.setChatroomId(loginOptional.get().getChatroomId());
			}
			loginProfile.setUserId(id.getUserId());
			loginPrefer.setUserId(id.getUserPreferId());
			matching.setUser(loginProfile);
			matching.setUserPrefer(loginPrefer);

			// 假如登入者喜歡對方，確認另一個使用者的配對狀態
			if (matching.isLikeOrNot()) {

				MatchingId checking = new MatchingId(id.getUserPreferId(), id.getUserId());
				Optional<Matching> likedUserOptional = mRep.findById(checking);
				if (likedUserOptional.isPresent()) {
					Matching likedUser = likedUserOptional.get();
					// 如果雙方都喜歡對方，則儲存配對成功時間
					if (likedUser.isLikeOrNot()) {
						System.out.println("likedUser:" + likedUser);
						likedUser.setMatchedSuccessfullyDate(LocalDate.now());
						matching.setMatchedSuccessfullyDate(LocalDate.now());

						// 如果配對成功且沒有創建過聊天室，則創建聊天室
						if (likedUser.getChatroomId() == null && matching.getChatroomId() == null) {
							List<UsersProfile> matchedUsers = new ArrayList<>();
							matchedUsers.add(loginProfile);
							matchedUsers.add(loginPrefer);
							Chatroom userPersonalChatroom = cService.createUserPersonalChatroom(matchedUsers);

							// 登入者儲存聊天室Id
							matching.setChatroomId(userPersonalChatroom.getChatroomId());
							matching.setChatroom(userPersonalChatroom);

							// 另一個配對者也儲存聊天室Id
							likedUser.setChatroomId(userPersonalChatroom.getChatroomId());
							likedUser.setChatroom(userPersonalChatroom);

						}
						mRep.save(likedUser);
						mRep.save(matching);
					}
				}
			}
			mRep.save(matching);
			return matching;
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			System.out.println("存不進去!!!!");
		}
		return matching;
	}

	public Matching getUserMatching(Integer userId, Integer userPreferId) {
		try {
			MatchingId matchingId = new MatchingId(userId, userPreferId);
			Optional<Matching> optional = mRep.findById(matchingId);
			if (optional.isPresent()) {
				Matching matching = optional.get();
				return matching;
			}
		} catch (Exception e) {
			System.out.println("找另外一個人的資料失敗");
		}
		return null;
	}

	public Integer countTodayMatchingQty(Integer loginId) {
		try {
			Long todayMatching = mRep.countByIdUserIdAndMatchedDate(loginId, LocalDate.now());
			return todayMatching != null ? Math.toIntExact(todayMatching) : 0;
		} catch (Exception e) {
			System.out.println("ops又出錯了");
			return 0;
		}
	}

	/** 查詢使用者和配對者今日是否配對過 */
	public Optional<Matching> findTodayMatching(Integer userId, Integer userPreferId) {
		LocalDate today = LocalDate.now();
		return mRep.findByIdUserIdAndIdUserPreferIdAndMatchedDate(userId, userPreferId, today);
	}

	public Integer getChatroomId(Integer userId, Integer userPreferId) {
		try {
			MatchingId matchingId = new MatchingId(userId, userPreferId);
			Optional<Matching> optional = mRep.findById(matchingId);
			if (optional.isPresent()) {
				Matching matching = optional.get();
				return matching.getChatroomId();
			}
		} catch (Exception e) {
			System.out.println("找另外一個人的資料失敗");
		}
		return null;
	}
	
	/** 計算配對成功數量 */
	public Integer countMutualMatching(Integer loginId) {
		try {
			Long mutualMatchingQty = mRep.countByIdUserIdAndMatchedSuccessfullyDateIsNotNull(loginId);
			return mutualMatchingQty != null ? Math.toIntExact(mutualMatchingQty) : 0;
		} catch (Exception e) {
			System.out.println("ops又出錯了");
			e.printStackTrace();
			return 0;
		}
		
	}
	
	public List<UsersProfile> getMutualMatchingList(Integer loginId){
		try {
			return mRep.findMatchedUsersProfilesByUserId(loginId);			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}