package com.hbxy.gmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*
springmvc.mxl、
    // 表示拦截所有
    <mvc:interceptors>
        <bean class="com.hbxy.gmall.config.AuthInterceptor">
    </mvc:interceptors>

     <mvc:interceptors>
         // 表示拦截所有
         // <bean class="com.hbxy.gmall.config.AuthInterceptor">
            <mvc: interceptor>
                  <mvc:mapping path="/**">
                  <bean class="com.hbxy.gmall.config.AuthInterceptor">
            </mvc: interceptor>
    </mvc:interceptors>
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
