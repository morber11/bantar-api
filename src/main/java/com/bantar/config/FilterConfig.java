package com.bantar.config;

import com.bantar.filter.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class FilterConfig {

    @Bean(name = "rateLimitingFilterBean")
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/*"); // global for now ? don't see a reason not to
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
