package com.meetu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.UsersProfile;
import com.meetu.model.UsersProfileRespository;

@Service
public class UsersProfileService {

	@Autowired
	private UsersProfileRespository usersProfileRepo;

	public List<UsersProfile> getAllUsers() {
		return usersProfileRepo.findAll();
	}

	public UsersProfile findByUserId(Integer userId) {
		Optional<UsersProfile> optional = usersProfileRepo.findById(userId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;

	}

	public List<UsersProfile> getMatchingCard(Integer userId) {
		UsersProfile currentUser = usersProfileRepo.findById(userId).orElse(null);
		if (currentUser == null) {
			return List.of(); // 返回空列表如果用户不存在
		}

		Integer minAge = currentUser.getUserPreferAgeMin();
		Integer maxAge = currentUser.getUserPreferAgeMax();
		Character preferGender = currentUser.getUserPreferGen();
		System.out.println("minAge:" + minAge);
		System.out.println("maxAge:" + maxAge);
		System.out.println("preferGender:" + preferGender);

		// 检查是否设置了所有必要的偏好
		if (minAge == null || maxAge == null || preferGender == null) {
			return List.of(); // 如果偏好未设置，返回空列表
		}
		try {
			List<UsersProfile> matchingCards = usersProfileRepo.findUsersWithinAgeRangeAndGenderPreference(minAge,
					maxAge, preferGender, userId);
			return matchingCards;

		} catch (Exception e) {
			System.out.println("出事了");
		}
		return null;
	}

}
