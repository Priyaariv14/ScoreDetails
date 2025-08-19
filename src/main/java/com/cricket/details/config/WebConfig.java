package com.cricket.details.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cricket.details.logging.SensitiveDataMaskingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SensitiveDataMaskingInterceptor sensitiveDataMaskingInterceptor;

    public WebConfig(SensitiveDataMaskingInterceptor sensitiveDataMaskingInterceptor) {
        this.sensitiveDataMaskingInterceptor = sensitiveDataMaskingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sensitiveDataMaskingInterceptor)
                .addPathPatterns("/**");
    }

}
