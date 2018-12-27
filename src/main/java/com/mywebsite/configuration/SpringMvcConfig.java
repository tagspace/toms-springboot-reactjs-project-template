package com.mywebsite.configuration;

import com.mywebsite.configuration.interceptors.RequestLoggerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggerInterceptor());
    }

    @Bean
    AbstractRequestLoggingFilter requestLoggingFilter() {
        /**
         * This Filter wraps the requests in a ContentCachingRequestWrapper, which is used by RequestLoggerInterceptor to extract and log the payload
         */
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludePayload(true);
        return filter;
    }
}
