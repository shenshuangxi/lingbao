package com.sundy.lingbao.client.spring.annotation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import com.sundy.lingbao.client.ConfigService;
import com.sundy.lingbao.client.config.Config;
import com.sundy.lingbao.core.consts.GloablConst;
import com.sundy.lingbao.core.exception.LingbaoException;

public class LingbaoConfigPropertiesRefresh  implements ApplicationContextAware {

	private final static Logger logger = LoggerFactory.getLogger(LingbaoConfigPropertiesRefresh.class);
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	private PropertySourceFactory propertySourceFactory = new DefaultPropertySourceFactory();
	
	private ConfigurableApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}
	
	@LingbaoConfigChangeListener(ordered=Integer.MIN_VALUE)
	public void configChange() {
		if (applicationContext!=null) {
			MutablePropertySources mutablePropertySources = applicationContext.getEnvironment().getPropertySources();
			Config config = ConfigService.getConfig();
			CompositePropertySource composite = new CompositePropertySource(GloablConst.LINGBAO_PROPERTY_SOURCE_NAME);
			for (String location : config.getLocations()) {
				try {
					String resolvedLocation = applicationContext.getEnvironment().resolveRequiredPlaceholders(location);
					Resource resource = this.resourceLoader.getResource(resolvedLocation);
					composite.addPropertySource(propertySourceFactory.createPropertySource(null, new EncodedResource(resource, StandardCharsets.UTF_8)));
				}
				catch (IllegalArgumentException | FileNotFoundException | UnknownHostException ex) {
					// Placeholders not resolvable or resource not found when trying to open it
					if (logger.isInfoEnabled()) {
						logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
					}
				} catch (IOException e) {
					logger.info(e.getMessage(), e);
					throw new LingbaoException(-1, String.format("read [%s] file error", location));
				}
			}
			if (mutablePropertySources.contains(GloablConst.LINGBAO_PROPERTY_SOURCE_NAME)) {
				mutablePropertySources.replace(GloablConst.LINGBAO_PROPERTY_SOURCE_NAME, composite);
			} else {
				mutablePropertySources.addLast(composite);
			}
		}
	}
	
}
