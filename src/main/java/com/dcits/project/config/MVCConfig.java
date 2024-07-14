package com.dcits.project.config;

import com.dcits.project.aop.AuthorityValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MVCConfig implements WebMvcConfigurer {
    @Autowired
    private AuthorityValidation authorityValidation;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorityValidation).addPathPatterns("/**");
    }

//    @Bean
//    public FilterRegistrationBean registFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new Myfilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("filter");
//        registration.setOrder(1);
//        return registration;
//    }

}