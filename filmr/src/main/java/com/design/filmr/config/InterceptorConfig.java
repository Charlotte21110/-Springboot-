package com.design.filmr.config;

import com.design.filmr.interceptor.PermissionInterceptor;
import com.design.filmr.utils.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public HandlerInterceptor getPermissionInterceptor(){
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.getPermissionInterceptor());
    }
    @Bean
    public SpringUtils getSpringUtils(ApplicationContext applicationContext) {
        SpringUtils instance = SpringUtils.getInstance();
        SpringUtils.setApplicationContext(applicationContext);
        return instance;
    }
}
