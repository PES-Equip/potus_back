package com.potus.app.security;

import com.potus.app.security.filter.ConfirmedUserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ConfirmedUserFilter> userNormalFilter(){
        FilterRegistrationBean<ConfirmedUserFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ConfirmedUserFilter());

        registrationBean.addUrlPatterns("/api/potus/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
