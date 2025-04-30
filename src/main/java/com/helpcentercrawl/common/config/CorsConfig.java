package com.helpcentercrawl.common.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * packageName    : com.helpcentercrawl.common.config
 * fileName       : CorsConfig
 * author         : MinKyu Park
 * date           : 25. 4. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 25.        MinKyu Park       최초 생성
 */
@Component
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://dashboard.dev5.com")
                .allowedOrigins("http://helpcenter.dev5.com")
                .allowedOrigins("http://192.168.1.55:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
