package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialFollowRepository extends JpaRepository<SocialFollow, SocialFollowId> {

	@Query("FROM SocialFollow sf WHERE sf.follower.userId = :followerId")
	List<SocialFollow> findSocialFollowByFollowerId(@Param("followerId") Integer followerId);

	@Query("SELECT COUNT(sf) FROM SocialFollow sf WHERE sf.follower.userId = :followerId")
	Long countSocialFollowByFollowerId(@Param("followerId") Integer followerId);

	@Query("SELECT COUNT(sf) FROM SocialFollow sf WHERE sf.followee.userId = :followeeId")
	Long countSocialFollowByFolloweeId(@Param("followeeId") Integer followeeId);

	/** 查詢互相追蹤數量 已廢棄不用 */
	@Query("SELECT COUNT(sf1) FROM SocialFollow sf1 " + "WHERE sf1.follower.id = :followerId " + "AND EXISTS ("
			+ "    SELECT 1 FROM SocialFollow sf2 " + "    WHERE sf2.follower.id = sf1.followee.id "
			+ "    AND sf2.followee.id = :followerId" + ")")
	Integer countMutualFollowByFollowerId(@Param("followerId") Integer followerId);

	/** 查詢互相追蹤清單 已廢棄不用 */
	@Query("SELECT f.followee.id FROM SocialFollow f WHERE f.follower.id = :followerId "
			+ "AND f.followee.id IN (SELECT f2.follower.id FROM SocialFollow f2 WHERE f2.followee.id = :followerId)")
	List<UsersProfile> findMutualFollowIds(@Param("followerId") Integer followerId);

	
	/**
     * 查詢指定用戶ID追蹤中的用戶清單。
     *
     * @param userId 指定的用戶ID
     * @return 指定用戶追蹤中的用戶清單
     */
    @Query("SELECT sf.followee FROM SocialFollow sf " +
           "WHERE sf.id.followerId = :userId ")
    List<UsersProfile> findFolloweeUsersProfileByUserId(@Param("userId") Integer followerId);
    
    
    /**
     * 查詢追蹤指定用戶ID的用戶清單。
     *
     * @param userId 指定的用戶ID
     * @return 追蹤指定用戶的用戶清單
     */
    @Query("SELECT sf.follower FROM SocialFollow sf " +
    		"WHERE sf.id.followeeId = :userId ")
    List<UsersProfile> findFollowerUsersProfileByUserId(@Param("userId") Integer followeeId);
}
