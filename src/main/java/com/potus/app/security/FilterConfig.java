package com.potus.app.security;

import com.potus.app.admin.service.AdminService;
import com.potus.app.potus.service.PotusRegistryService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.security.filter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    PotusService potusService;

    @Autowired
    PotusRegistryService potusRegistryService;

    @Autowired
    AdminService adminService;

    @Bean
    public FilterRegistrationBean<ConfirmedUserFilter> userNormalFilter(){
        FilterRegistrationBean<ConfirmedUserFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ConfirmedUserFilter());

        registrationBean.addUrlPatterns("/api/potus/*","/api/user/profile");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PotusStatesFilter> potusStatesFilter(){
        FilterRegistrationBean<PotusStatesFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new PotusStatesFilter(potusService));

        registrationBean.addUrlPatterns("/api/potus/*", "/api/user/profile");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PotusIsDeadFilter> potusIsDeadFilter(){
        FilterRegistrationBean<PotusIsDeadFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new PotusIsDeadFilter(potusRegistryService));

        registrationBean.addUrlPatterns("/api/potus/*");
        registrationBean.setOrder(4);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ExternalTokenFilter> externalTokenFilter(){
        FilterRegistrationBean<ExternalTokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ExternalTokenFilter(adminService));

        registrationBean.addUrlPatterns("/api/external/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean<AdminTokenFilter> adminTokenFilter(){
        FilterRegistrationBean<AdminTokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AdminTokenFilter());

        registrationBean.addUrlPatterns("/api/admin/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }


}
