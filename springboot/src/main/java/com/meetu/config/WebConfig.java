package com.meetu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${post.image.upload.dir}")
    private String imageUploadDir;
    
    @Value("${post.video.upload.dir}")
    private String videoUploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        
        // 映射圖片資源
        registry.addResourceHandler("/media/posts/images/**")
                .addResourceLocations("file:" + imageUploadDir + "/");

        // 映射影片資源
        registry.addResourceHandler("/media/posts/videos/**")
                .addResourceLocations("file:" + videoUploadDir + "/");
    }
}
