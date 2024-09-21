package com.meetu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.Users;
import com.meetu.model.UsersRepository;
import com.meetu.model.managersManage.ManagersManage;
import com.meetu.model.managersManage.ManagersManageRepository;

@Service
public class ManagersManageService {

	@Autowired
	public ManagersManageRepository mmRep;

	@Autowired
	public UsersRepository uRep;

	public Integer getMatchingQty(Integer loginId) {
		try {
            Optional<Users> userOptional = uRep.findById(loginId);
            if (userOptional.isPresent()) {
                Character vip = userOptional.get().getVip();

                //目前只有版本1
                Optional<ManagersManage> optional = mmRep.findById(1);
                
                if (optional.isPresent()) {
                    ManagersManage manage = optional.get();
                    if (vip == '0') {
                        return manage.getNonVipMatchingQty();
                    } else if (vip == '1') {
                        return manage.getVipMatchingQty();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

}