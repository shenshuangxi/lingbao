package com.sundy.lingbao.common.controller;

import java.util.List;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig  implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	    PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
	    pageResolver.setFallbackPageable(PageRequest.of(0, 10));
	    argumentResolvers.add(pageResolver);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	    configurer.favorPathExtension(false);
	    configurer.ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON);
	}
	
	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		MimeMappings mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
		mimeMappings.add("html", "text/html;charset=utf-8");
		factory.setMimeMappings(mimeMappings);
	}

}
