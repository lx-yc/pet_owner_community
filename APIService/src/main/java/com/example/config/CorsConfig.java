package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
                .allowedOriginPatterns("*") // 允许所有来源
                .allowCredentials(true) // 允许cookie
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许方法
                .allowedHeaders("*") // 允许请求头
                .maxAge(3600); // 预检有效期
    }
}
