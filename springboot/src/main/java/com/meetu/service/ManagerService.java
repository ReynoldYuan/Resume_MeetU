package com.meetu.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.meetu.model.Managers;
import com.meetu.model.ManagersRepository;

@Service
public class ManagerService {

	@Autowired
	private ManagersRepository managersRepo;
	
	@Value("${file.upload-dir2}")
	private String uploadDir;
	
	@Value("${domain.url}")
	private String domainUrl;
	
	@Autowired
	private PasswordEncoder pwdEncoder;
	
	public Managers checkManagerLogin(String email, String password) {
		Managers manager = managersRepo.findByEmail(email);
		if(manager != null && pwdEncoder.matches(password, manager.getPassword())) {
			String managerPics = manager.getPicture();
			manager.setPicture(domainUrl+managerPics);
			return manager;
		}
		else {
			return null;
		}		
	}
	
	public boolean checkEmailIsExist(String email) {
		Managers manager = managersRepo.findByEmail(email);
		
		if(manager != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean registerManager(Managers manager, MultipartFile picture) {
		String encodePwd = pwdEncoder.encode(manager.getPassword());
		manager.setPassword(encodePwd);
		
		if(picture != null && !picture.isEmpty()) {
			String cleanPath = StringUtils.cleanPath(picture.getOriginalFilename());
			String filenameExtension = StringUtils.getFilenameExtension(cleanPath);
			String newFilename = UUID.randomUUID().toString() + "." + filenameExtension;
			String filePath = uploadDir + newFilename;
			try {
				Path uploadPath = Paths.get(uploadDir);
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Path path = Paths.get(filePath);
				Files.copy(picture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				manager.setPicture("/images/managersPics/" + newFilename);
			}catch (IOException e) {
				throw new RuntimeException("無法保存圖片文件", e);
			}
		}
		
		Managers save = managersRepo.save(manager);
		if(save != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public List<Managers> showAllManagers(){
		List<Managers> all = managersRepo.findAll();
		for(Managers manager : all) {
			String managerPics = manager.getPicture();
			manager.setPicture(domainUrl+managerPics);
		}
		return all;
	}
	
	public Managers showManager(Integer managerId) {
		Optional<Managers> optional = managersRepo.findById(managerId);
		if(optional.isPresent()) {
			Managers manager = optional.get();
			if(manager.getDeleteState() == (byte)1) {
				return null;
			}
			String managerPics = manager.getPicture();
			manager.setPicture(domainUrl+managerPics);
			return manager;
		}
		else
			return null;
	}
	
	public boolean updateManagerProfile(Managers update, MultipartFile picture) {
		Optional<Managers> optional = managersRepo.findById(update.getManagerId());
		if(optional.isPresent()) {
			Managers origin = optional.get();
			if(!origin.getPassword().equals(update.getPassword())) {
				String encodePwd = pwdEncoder.encode(update.getPassword());
				update.setPassword(encodePwd);
			}
		}
		if(picture != null && !picture.isEmpty()) {
			String cleanPath = StringUtils.cleanPath(picture.getOriginalFilename());
			String filenameExtension = StringUtils.getFilenameExtension(cleanPath);
			String newFilename = UUID.randomUUID().toString() + "." + filenameExtension;
			String filePath = uploadDir + newFilename;
			try {
				Path uploadPath = Paths.get(uploadDir);
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Path path = Paths.get(filePath);
				Files.copy(picture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				update.setPicture("/images/managersPics/" + newFilename);
			}catch (IOException e) {
				throw new RuntimeException("無法保存圖片文件", e);
			}
		}
		
		Managers save = managersRepo.save(update);
		if(save != null) {
			return true;
		}
		else {
			return false;
		}	
	}
	
	public boolean updateManagerPassword(String managerMail, String newPassword) {
		Managers manager = managersRepo.findByEmail(managerMail);
		String encodePwd = pwdEncoder.encode(newPassword);
		manager.setPassword(encodePwd);
		Managers save = managersRepo.save(manager);
		if(save != null) {
			return true;
		}
		else
			return false;
	}
	
	public boolean deleteManager(Managers manager) {
		manager.setDeleteState((byte)1);
		Managers save = managersRepo.save(manager);
		if(save != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
