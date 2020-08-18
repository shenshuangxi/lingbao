package com.sundy.lingbao.db.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sundy.db.command.AnnotationCommandHandlerAdapter;
import com.sundy.db.command.CommandBus;


public class CommandBusPostProcessor implements BeanPostProcessor, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		CommandBus commandBus = this.applicationContext.getBean(CommandBus.class);
		new AnnotationCommandHandlerAdapter<Object>(bean,commandBus);
		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
