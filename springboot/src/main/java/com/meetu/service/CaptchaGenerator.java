package com.meetu.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class CaptchaGenerator {

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 6;
    private final Random random = new Random();
    private String currentCaptcha;

    public String generateCaptcha() {
        StringBuilder captcha = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            captcha.append(CHARACTERS.charAt(index));
        }
        currentCaptcha = captcha.toString();
        return captcha.toString();
    }
    
    public boolean validateCaptcha(String inputCaptcha) {
        return inputCaptcha != null && inputCaptcha.equals(currentCaptcha);
    }
	
}
