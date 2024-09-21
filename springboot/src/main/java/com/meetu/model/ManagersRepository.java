package com.meetu.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagersRepository extends JpaRepository<Managers, Integer> {

	Managers findByEmail(String email);
	
	Managers findByEmailAndPassword(String email, String password);
}
