package org.myweb.first.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 리액트 (http://localhost:3000) 에서 오는 요청을 받아 주도록 cross origin 해결 설정임
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")   //모든 경로에 대해서
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:3001",
                        "http://localhost:3002")  //허용할 도메인 지정
//                .allowedOrigins("http://localhost:3001")  //허용할 도메인 지정
//                .allowedOrigins("http://localhost:3002")  //허용할 도메인 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //허용할 http 전송방식
                .allowedHeaders("*")   //허용할 해더
                .exposedHeaders("*")
                .allowCredentials(true);  //쿠키 허용 여부
    }
}
