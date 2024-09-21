package com.meetu.model.matching;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.meetu.model.UsersProfile;

public interface MatchingRepository extends JpaRepository<Matching, MatchingId> {

	/** 計算特定用戶在特定日期的匹配數量, 以計算當日已配對過數量 */
	long countByIdUserIdAndMatchedDate(Integer userId, LocalDate matchedDate);

	/** 查找特定 userId 和 userPreferId 的當天匹配記錄 */
	Optional<Matching> findByIdUserIdAndIdUserPreferIdAndMatchedDate(Integer userId, Integer userPreferId,
			LocalDate matchedDate);

	/** 用配對成功日期數量計算用戶配對成功的數量 */
	Long countByIdUserIdAndMatchedSuccessfullyDateIsNotNull(Integer userId);
	
	
	 /**
     * 查詢指定用戶ID成功匹配的所有用戶資料。
     *
     * @param userId 指定的用戶ID
     * @return 與指定用戶成功匹配的用戶資料列表
     */
    @Query("SELECT m.userPrefer FROM Matching m " +
           "WHERE m.id.userId = :userId " +
           "AND m.matchedSuccessfullyDate IS NOT NULL")
    List<UsersProfile> findMatchedUsersProfilesByUserId(@Param("userId") Integer userId);
}
