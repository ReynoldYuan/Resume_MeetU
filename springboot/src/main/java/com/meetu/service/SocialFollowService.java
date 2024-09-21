package com.meetu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meetu.model.SocialFollow;
import com.meetu.model.SocialFollowId;
import com.meetu.model.SocialFollowRepository;
import com.meetu.model.Users;
import com.meetu.model.UsersProfile;
import com.meetu.model.UsersRepository;


@Service
@Transactional
public class SocialFollowService {
	
	@Autowired
	public SocialFollowRepository sfRep;
	
	@Autowired
	public UsersRepository uRep;
	
	@Autowired
	public UsersProfileService upService;
	
	public boolean checkIfFollow(Integer loggedUserId, Integer followeeId) {
		Optional<SocialFollow> optional = sfRep.findById(new SocialFollowId(loggedUserId,followeeId));
		if(optional.isPresent()) {
			return true;
		}
		return false;
	}
	
    public Long countFollowee(Integer followerId) {
		return sfRep.countSocialFollowByFollowerId(followerId);    	
    }
    
    public Long countFollower(Integer followeeId) {
		return sfRep.countSocialFollowByFolloweeId(followeeId);    	
    }
    
    /** 已廢棄不用*/
    public Integer countMutualFollow(Integer followerId) {
    	return sfRep.countMutualFollowByFollowerId(followerId);
    }
    
    public SocialFollow follow(SocialFollowId sfId) {
    	UsersProfile follower = upService.findByUserId(sfId.getFollowerId());
    	UsersProfile followee = upService.findByUserId(sfId.getFolloweeId());
    	SocialFollow sf = new SocialFollow();
    	sf.setId(sfId);    	
    	sf.setFollower(follower);
    	sf.setFollowee(followee);
        return sfRep.save(sf);
    }
    
    public void unfollow(SocialFollowId sfId) {
    	UsersProfile follower = upService.findByUserId(sfId.getFollowerId());
    	UsersProfile followee = upService.findByUserId(sfId.getFolloweeId());
    	SocialFollow sf = new SocialFollow();
    	sf.setId(sfId);    	
    	sf.setFollower(follower);
    	sf.setFollowee(followee);
    	sfRep.delete(sf);
    }
    
    /**查詢互相追蹤好友名單 已廢棄不用*/
    public List<UsersProfile> getMutualFollowList(Integer userId) {
        return sfRep.findMutualFollowIds(userId);
    }
    
    /**查詢追蹤中清單*/
    public List<UsersProfile> getFolloweeList(Integer followerId){
    	try {
			return sfRep.findFolloweeUsersProfileByUserId(followerId);			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**查詢粉絲清單*/
    public List<UsersProfile> getFollowerList(Integer followeeId){
    	try {
			return sfRep.findFollowerUsersProfileByUserId(followeeId);			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    
}
