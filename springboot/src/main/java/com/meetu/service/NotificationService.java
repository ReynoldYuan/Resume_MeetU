package com.meetu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.Notification;
import com.meetu.model.NotificationRespository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NotificationService {

	@Autowired
	private NotificationRespository notiRepo;
	
	
	public Notification saveNotification(Notification notification) {
		
		return notiRepo.save(notification);
		
	}
	
	public Notification updateNoti(Notification notification) {
		return notiRepo.save(notification);
	}
	
	
	
	public void deleteNotiById(Integer id) {
		notiRepo.deleteById(id);
	}
	
	
	public Notification fingNotiById(Integer id) {
		
		Optional<Notification> optional = notiRepo.findById(id);
		
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public List<Notification> findAllNoti(){
		return notiRepo.findAll();
	}
	
	
	public List<Notification> findNotiByUserId(Integer userId) {
	    return notiRepo.findByUsers_UserId(userId);
	}
	
	public List<Notification> findLatestNotiByUserId(Integer userId) {
        return notiRepo.findTop5ByUserId(userId);
    }
	
	public void markNoti(Integer id) {
		Optional<Notification> optional = notiRepo.findById(id);
		
		if(optional.isPresent()) {
			Notification notification = optional.get();
			notification.setNotificationRead('1');
			notiRepo.save(notification);
		}
	}
}
