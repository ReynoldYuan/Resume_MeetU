package com.meetu.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersBanRepository extends JpaRepository<UsersBan, UsersBanId>{

	List<UsersBan> findByIdUserId(Integer userId);
	
}
