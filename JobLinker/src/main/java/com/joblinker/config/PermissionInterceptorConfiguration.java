package com.joblinker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/", "/api/v1/auth/**", "/storage/**"
                ,"/api/v1/companies/by-employer/**", "/api/v1/jobs/by-company/**",
                 "/api/v1/skills/**", "/api/v1/files",
                "/api/v1/resumes/**", "/api/v1/subscribers/**",
                "/api/v1/companies/top-companies",
                "/api/v1/jobs/top-jobs",
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}