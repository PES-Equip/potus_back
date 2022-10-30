package com.potus.app.security;

import com.potus.app.potus.service.PotusService;
import com.potus.app.security.filter.ConfirmedUserFilter;
import com.potus.app.security.filter.PotusIsDeadFilter;
import com.potus.app.security.filter.PotusStatesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    PotusService potusService;

    @Bean
    public FilterRegistrationBean<ConfirmedUserFilter> userNormalFilter(){
        FilterRegistrationBean<ConfirmedUserFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ConfirmedUserFilter());

        registrationBean.addUrlPatterns("/api/potus/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PotusStatesFilter> potusStatesFilter(){
        FilterRegistrationBean<PotusStatesFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new PotusStatesFilter(potusService));

        registrationBean.addUrlPatterns("/api/potus/*","/api/user/profile");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PotusIsDeadFilter> potusIsDeadFilter(){
        FilterRegistrationBean<PotusIsDeadFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new PotusIsDeadFilter());

        registrationBean.addUrlPatterns("/api/potus/*");
        registrationBean.setOrder(4);
        return registrationBean;
    }
}
