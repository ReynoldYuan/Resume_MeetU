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

import com.meetu.model.Users;
import com.meetu.model.UsersRepository;

@Service
public class UserService {

	@Autowired
	private UsersRepository usersRepo;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Value("${domain.url}")
	private String domainUrl;
	
	@Autowired
	private PasswordEncoder pwdEncoder;
	
	public Users checkUsersLogin(String userMail, String userPwd) {
		Users user = usersRepo.findByUserMail(userMail);
		if(user != null && pwdEncoder.matches(userPwd, user.getUserPwd())) {
			String userPics = user.getUsersProfile().getUserPics();
			user.getUsersProfile().setUserPics(domainUrl+userPics);
			return user;
		}
		else {
			return null;
		}
//		Users user = usersRepo.findByUserMailAndPwd(userMail, userPwd);
		
	}
	
	public boolean checkMailIsExist(String userMail) {
		Users user = usersRepo.findByUserMail(userMail);
		
		if(user != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean registerUser(Users user, MultipartFile userPic) {
		String encodePwd = pwdEncoder.encode(user.getUserPwd());
		user.setUserPwd(encodePwd);
		
		if(userPic != null && !userPic.isEmpty()) {
			String cleanPath = StringUtils.cleanPath(userPic.getOriginalFilename());
			String filenameExtension = StringUtils.getFilenameExtension(cleanPath);
			String newFilename = UUID.randomUUID().toString() + "." + filenameExtension;
			String filePath = uploadDir + newFilename;
			try {
				Path uploadPath = Paths.get(uploadDir);
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Path path = Paths.get(filePath);
				Files.copy(userPic.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.getUsersProfile().setUserPics("/images/userPics/" + newFilename);
			}catch (IOException e) {
				throw new RuntimeException("無法保存圖片文件", e);
			}
		}
		
		Users save = usersRepo.save(user);
		if(save != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public List<Users> showAllUsers(){
		List<Users> all = usersRepo.findAll();
		for(Users user : all) {
			String userPics = user.getUsersProfile().getUserPics();
			user.getUsersProfile().setUserPics(domainUrl+userPics);
		}
		return all;
	}
	
	public Users showUser(Integer userId) {
		Optional<Users> optional = usersRepo.findById(userId);
		if(optional.isPresent()) {
			Users user = optional.get();
			if(user.getDeleteState() == (byte)1) {
				return null;
			}
			String userPics = user.getUsersProfile().getUserPics();
			user.getUsersProfile().setUserPics(domainUrl+userPics);
			return user;
		}
		else
			return null;
	}
	
	public boolean updateUserProfile(Users update, MultipartFile userPic) {
		Optional<Users> optional = usersRepo.findById(update.getUserId());
		if(optional.isPresent()) {
			Users origin = optional.get();
			if(!origin.getUserPwd().equals(update.getUserPwd())) {
				String encodePwd = pwdEncoder.encode(update.getUserPwd());
				update.setUserPwd(encodePwd);
			}
		}
		if(userPic != null && !userPic.isEmpty()) {
			String cleanPath = StringUtils.cleanPath(userPic.getOriginalFilename());
			String filenameExtension = StringUtils.getFilenameExtension(cleanPath);
			String newFilename = UUID.randomUUID().toString() + "." + filenameExtension;
			String filePath = uploadDir + newFilename;
			try {
				Path uploadPath = Paths.get(uploadDir);
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Path path = Paths.get(filePath);
				Files.copy(userPic.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				update.getUsersProfile().setUserPics("/images/userPics/" + newFilename);
			}catch (IOException e) {
				throw new RuntimeException("無法保存圖片文件", e);
			}
		}
		
		Users save = usersRepo.save(update);
		if(save != null) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public boolean updateUserPassword(String userMail, String newPassword) {
		Users user = usersRepo.findByUserMail(userMail);
		String encodePwd = pwdEncoder.encode(newPassword);
		user.setUserPwd(encodePwd);
		Users save = usersRepo.save(user);
		if(save != null) {
			return true;
		}
		else
			return false;
	}
	
	public boolean deleteUser(Users user) {
		user.setDeleteState((byte)1);
		String substring = user.getUsersProfile().getUserPics().substring(27);
		user.getUsersProfile().setUserPics(substring);
		Users save = usersRepo.save(user);
		if(save != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean recoverUserByManager(String userMail) {
		Users user = usersRepo.findByUserMail(userMail);
		if(user.getDeleteState() != 1 && user.getUserIsBan() != 'R') {
			return false;
		}
		else {
			user.setUserIsBan('N');
			user.setDeleteState((byte)0);
			usersRepo.save(user);
			return true;
		}
	}
	
	// 更新使用者檢舉狀態
	public Users updateUser(Users user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("使用者或使用者ID不能為空");
        }
        
        Users existingUser = usersRepo.findById(user.getUserId()).orElse(null);
        if (existingUser == null) {
            throw new IllegalArgumentException("找不到指定ID的使用者");
        }
        
        existingUser.setUserIsBan(user.getUserIsBan());
        return usersRepo.save(existingUser);
    }
	
}
