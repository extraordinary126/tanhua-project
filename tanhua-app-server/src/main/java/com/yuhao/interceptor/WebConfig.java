package com.yuhao.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor()).
                addPathPatterns("/**").     //要拦截的路径
                excludePathPatterns(new String[]{"/user/login","/user/loginVerification"});  //不拦截的路径
    }
}
