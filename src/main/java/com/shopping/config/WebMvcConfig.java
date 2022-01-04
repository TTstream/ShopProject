package com.shopping.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//업로드한 파일을 읽어올 경로를 설정하는 클래스
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}") //application.properties에 설정한 "uploadPath" 프로퍼티 값을 읽어옴
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath); //로컬 컴퓨터에 저장된 파일을 읽어올 root 경로 설정
        // addResourceHandler() : 매핑 URI 설정
        // addResourceLocations() : 정적 리소스 위치 설정
    }

}
