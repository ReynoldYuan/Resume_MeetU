package com.meetu.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meetu.model.UsersBan;
import com.meetu.model.UsersBanDTO;
import com.meetu.model.UsersBanId;
import com.meetu.model.UsersBanRepository;
import com.meetu.model.UsersProfile;
import com.meetu.model.UsersRepository;

@Service
@Transactional
public class UsersBanService {

	@Autowired
	public UsersBanRepository uBanRepo;
	
	@Autowired
	public UsersRepository uRepo;
	
	@Autowired
	public UsersProfileService uProService;
	
	
	public UsersBan ban(UsersBanId uBanId) {
		
		UsersProfile userId = uProService.findByUserId(uBanId.getUserId());
		UsersProfile banedUserId = uProService.findByUserId(uBanId.getBanedUserId());
		
		UsersBan uBan = new UsersBan();
		uBan.setId(uBanId);
		uBan.setUserId(userId);
		uBan.setBanedUserId(banedUserId);
		
		return uBanRepo.save(uBan);
	}

	
	public void unBan(UsersBanId uBanId) {
		
		UsersProfile userId = uProService.findByUserId(uBanId.getUserId());
		UsersProfile banedUserId = uProService.findByUserId(uBanId.getBanedUserId());
		
		UsersBan uBan = new UsersBan();
		uBan.setId(uBanId);
		uBan.setUserId(userId);
		uBan.setBanedUserId(banedUserId);
		
		uBanRepo.delete(uBan);
	}
	
	public boolean checkBan(Integer loggedUserId, Integer userId) {
		Optional<UsersBan> optional = uBanRepo.findById(new UsersBanId(loggedUserId, userId));
	
		if(optional.isPresent()) {
			return true;
		}
		
		return false;
	}
	
	public List<UsersBanDTO> getBanList(Integer userId) {
        List<UsersBan> banList = uBanRepo.findByIdUserId(userId);
        return banList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UsersBanDTO convertToDTO(UsersBan usersBan) {
        UsersProfile banedUser = usersBan.getBanedUserId();
        String banedUserName = (banedUser != null) ? banedUser.getUserName() : "Unknown User";
        
        return new UsersBanDTO(
            usersBan.getId().getUserId(),
            usersBan.getId().getBanedUserId(),
            banedUserName
        );
    }
	
	
	
	
}
