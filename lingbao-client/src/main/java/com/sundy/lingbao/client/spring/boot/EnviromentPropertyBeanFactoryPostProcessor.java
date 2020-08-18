package com.sundy.lingbao.client.spring.boot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
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

public class EnviromentPropertyBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final static Logger logger = LoggerFactory.getLogger(EnviromentPropertyBeanFactoryPostProcessor.class);
	
	private static ConfigurableApplicationContext applicationContext;
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	private PropertySourceFactory propertySourceFactory = new DefaultPropertySourceFactory();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Config config = ConfigService.getConfig();
		CompositePropertySource composite = new CompositePropertySource(GloablConst.LINGBAO_PROPERTY_SOURCE_NAME);
		for (String location : config.getLocations()) {
			try {
				String resolvedLocation = EnviromentPropertyBeanFactoryPostProcessor.applicationContext.getEnvironment().resolveRequiredPlaceholders(location);
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
		EnviromentPropertyBeanFactoryPostProcessor.applicationContext.getEnvironment().getPropertySources().addLast(composite);
	}
	

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		EnviromentPropertyBeanFactoryPostProcessor.applicationContext = applicationContext;
	}

	
	

}
