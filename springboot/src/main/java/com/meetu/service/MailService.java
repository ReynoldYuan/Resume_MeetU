package com.meetu.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	
	@Autowired
    private JavaMailSender mailSender;
	
	public void sendPlainText(Collection<String> receivers, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receivers.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("pls enter your mail here");

        mailSender.send(message);
    }
}
