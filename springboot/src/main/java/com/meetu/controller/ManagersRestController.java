package com.meetu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetu.model.Managers;
import com.meetu.service.ManagerService;

@RestController
@CrossOrigin
public class ManagersRestController {

	@Autowired
	private ManagerService managerService;
	
	@PostMapping("/managers/profile")
	public String register(@RequestPart String managerJson, @RequestPart MultipartFile picture) throws IOException {
		Managers manager = new ObjectMapper().readValue(managerJson, Managers.class);
		boolean exist = managerService.checkEmailIsExist(manager.getEmail());
		
		if(!exist) {
			boolean register = managerService.registerManager(manager, picture);
			if(register) {
				return "註冊成功";
			}
			else {
				return "註冊失敗";
			}
		}
		else {
			return "已經有此帳號，請重新輸入";
		}
	}
	
	@GetMapping("/managers/profile")
	public List<Managers> showAll() {
		return managerService.showAllManagers();
	}
	
	@GetMapping("/managers/profile/{managerId}")
	public Managers show(@PathVariable Integer managerId) {
		return managerService.showManager(managerId);
	}
	
	@PutMapping("/managers/profile")
	public String update(@RequestPart String managerJson, @RequestPart MultipartFile picture) throws IOException {
		Managers manager = new ObjectMapper().readValue(managerJson, Managers.class);
		boolean update = managerService.updateManagerProfile(manager, picture);
		if(update) {
			return "修改成功";
		}
		else {
			return "修改失敗";
		}
	}
	
	@DeleteMapping("/managers/profile/{managerId}")
	public String delete(@PathVariable Integer managerId) {
		Managers manager = managerService.showManager(managerId);
		if(manager != null) {
			boolean deleteManager = managerService.deleteManager(manager);
			if(deleteManager)
				return "刪除成功";
			else
				return "刪除失敗";
		}
		else
			return "無此使用者";
	}
	
}
