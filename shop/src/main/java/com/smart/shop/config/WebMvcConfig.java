package com.smart.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String[] PUBLIC_PATHS = {
                "/api/auth/**",
                "/error",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        };

        registry.addInterceptor(authInterceptor)
                .excludePathPatterns(PUBLIC_PATHS);
    }
}
