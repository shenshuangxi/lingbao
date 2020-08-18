package com.sundy.lingbao.db.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sundy.db.event.AnnotationEventListenerAdapter;
import com.sundy.db.event.EventBus;


public class EventBusPostProcessor implements BeanPostProcessor, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		EventBus eventBus = this.applicationContext.getBean(EventBus.class);
		new AnnotationEventListenerAdapter(bean,eventBus);
		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
