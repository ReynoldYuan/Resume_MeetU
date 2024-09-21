package com.meetu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.meetu.util.JsonWebTokenInterceptor;

@Configuration
public class SpringBootJavaConfig implements WebMvcConfigurer {

	@Autowired
	private JsonWebTokenInterceptor jsonWebTokenInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jsonWebTokenInterceptor)
		.addPathPatterns("/users/profile/*")
		.addPathPatterns("/secure/users/*")

//		.addPathPatterns("/activities/*")
		.addPathPatterns("/users/ban/*")
		 .addPathPatterns("/secure/notification/*")


		.addPathPatterns("/managers/profile/*")
		.addPathPatterns("/users/profile/recover/*");
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
