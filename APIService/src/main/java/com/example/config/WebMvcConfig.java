package com.example.config;

import com.example.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/Desktop/pet-community-uploads/";

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")   // 拦截所有
                .excludePathPatterns(
                        "/user/login",     // 登录放行
                        "/user/register",   // 注册放行
//                        "/user/detail/*",       // 查看用户详情放行
//                        "/post/list",       // 查看帖子列表放行
//                        "/post/detail/**",   // 查看帖子详情放行
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/error",
                        "/uploads/**"
                ); // 放行接口
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR);
    }
}
