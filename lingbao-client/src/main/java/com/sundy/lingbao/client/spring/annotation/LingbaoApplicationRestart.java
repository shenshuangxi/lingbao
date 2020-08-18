package com.sundy.lingbao.client.spring.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.sundy.lingbao.client.ConfigService;

public class LingbaoApplicationRestart implements ApplicationContextAware {

	private ConfigurableApplicationContext applicationContext;
	
	private final static Logger logger = LoggerFactory.getLogger(LingbaoConfigPropertiesRefresh.class);
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}
	
	@LingbaoConfigChangeListener(ordered=Integer.MIN_VALUE)
	public void configChange() {
		if (applicationContext!=null) {
			ConfigService.clearConfig();
			if (applicationContext instanceof GenericApplicationContext) {
				logger.error("can not refresh GenericApplicationContext");
			} else {
				ConfigService.clearConfig();
				applicationContext.refresh();
			}
			
		}
	}

}
