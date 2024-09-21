package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersProfileRespository extends JpaRepository<UsersProfile, Integer> {

	@Query(value = "SELECT up.* FROM users_profile up " +
	           "JOIN users u ON up.userId = u.userId " +
	           "WHERE DATEDIFF(YEAR, CONVERT(DATE, up.userBirth, 23), GETDATE()) BETWEEN :minAge AND :maxAge " +
	           "AND (:preferGender = 'N' OR up.userGender = :preferGender) " +
	           "AND up.userId != :currentUserId " +
	           "AND u.userIsBan = 'N' " +
	           "AND u.deleteState = 0", nativeQuery = true)
	    List<UsersProfile> findUsersWithinAgeRangeAndGenderPreference(
	        @Param("minAge") int minAge,
	        @Param("maxAge") int maxAge,
	        @Param("preferGender") Character preferGender,
	        @Param("currentUserId") Integer currentUserId
	    );
}
