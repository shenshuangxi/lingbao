package com.sundy.lingbao.common.controller;

import javax.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.sundy.lingbao.common.filter.PVStaticFilter;

/**
 * 定义一个字符编码过滤器UTF-8
 * @author Administrator
 *
 */
@Configuration
public class FilterConfiguration {

	@Bean
	public FilterRegistrationBean<CharacterEncodingFilter> encodingFilter() {
		FilterRegistrationBean<CharacterEncodingFilter> bean = new FilterRegistrationBean<CharacterEncodingFilter>();
		bean.setFilter(new CharacterEncodingFilter());
		bean.setName("characterEncodingFilter");
		bean.addUrlPatterns("/*");
		bean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean<PVStaticFilter> pvStaticsFilter() {
		FilterRegistrationBean<PVStaticFilter> bean = new FilterRegistrationBean<PVStaticFilter>();
		bean.setFilter(new PVStaticFilter());
		bean.setName("pvStaticsFilter");
		bean.addUrlPatterns("/*");
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		bean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
		return bean;
	}
	
}
