package com.meetu.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.service.CaptchaGenerator;
import com.meetu.service.MailService;

@RestController
@CrossOrigin
public class MailController {

	@Autowired
	private MailService mailService;
	
	@Autowired
	private CaptchaGenerator captchaGenerator;
	
	@GetMapping("/users/sendMail")
	public String sendMail() {
		
		String captcha = captchaGenerator.generateCaptcha();
		
		Collection<String> receivers = List.of("pls enter your google mail");
		String subject = "MeetU修改密碼驗證信";
		String content = "哈囉，我們收到您的修改密碼請求，如果是您本人的操作，請回到本網站輸入此驗證碼，如果不是您本人，請聯繫我們，驗證碼為 : " + captcha;
		mailService.sendPlainText(receivers, subject, content);
		
		return "發送成功";
	}
	
}
