package com.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.filter.TestFilter;

@Configuration
public class CustomFilterConfig {
	
	@Bean
	public FilterRegistrationBean<TestFilter> testFilter(){
		FilterRegistrationBean<TestFilter> filterRegistration = new FilterRegistrationBean<TestFilter>();
        filterRegistration.setFilter(new TestFilter());
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(1);
        return filterRegistration;
	}

}
