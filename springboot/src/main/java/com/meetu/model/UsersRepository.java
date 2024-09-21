package com.meetu.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersRepository extends JpaRepository<Users, Integer> {

	Users findByUserMail(String userMail);
	
	@Query("from Users where userMail = :mail and userPwd = :pwd")
	Users findByUserMailAndPwd(@Param("mail") String userMail, @Param("pwd") String userPwd);
	
}
