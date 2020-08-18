package com.sundy.lingbao.db.spring.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.sundy.db.DbContext;
import com.sundy.db.command.CommandBus;
import com.sundy.db.command.SimpleCommandBus;
import com.sundy.db.event.EventBus;
import com.sundy.db.event.SimpleEventBus;
import com.sundy.lingbao.core.idgenerate.MachineIdGenerator;
import com.sundy.lingbao.db.spring.CommandBusPostProcessor;
import com.sundy.lingbao.db.spring.EventBusPostProcessor;
import com.sundy.lingbao.db.spring.config.DbConfig;
import com.sundy.lingbao.db.spring.util.BeanRegistrationUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class LingbaoDbRegister implements ImportBeanDefinitionRegistrar {

	@SuppressWarnings("serial")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableLingbaoDb.class.getName()));
		Map<String, Object> DbConfigProperties = new HashMap<String, Object>();
		DbConfigProperties.put("workerId", attributes.getString("workerId"));
		DbConfigProperties.put("datacenterId", attributes.getString("datacenterId"));
		DbConfigProperties.put("eventDir", attributes.getString("eventDir"));
		DbConfigProperties.put("fileEventCount", attributes.getNumber("fileEventCount"));
		DbConfigProperties.put("eventThreads", attributes.getNumber("eventThreads"));
		DbConfigProperties.put("queryDataSourcePropertyPath", attributes.getString("queryDataSourcePropertyPath"));
		DbConfigProperties.put("eventDataSourcePropertyPath", attributes.getString("eventDataSourcePropertyPath"));
		BeanRegistrationUtil.registerBeanDefinitionWithPropertyValuesIfNotExists(registry, DbConfig.class.getName(), DbConfig.class, DbConfigProperties);

		// regsiter dbContext
		BeanRegistrationUtil.registerBeanDefinitionWithConstructValuesIfNotExists(registry, DbContext.class.getName(), DbContext.class, new ArrayList<Object>() {
			{
				HikariConfig queryDataSourceConfig = new HikariConfig(attributes.getString("queryDataSourcePropertyPath"));
				HikariDataSource queryDataSource = new HikariDataSource(queryDataSourceConfig);
				add(queryDataSource);

				HikariConfig eventDataSourceConfig = new HikariConfig(attributes.getString("eventDataSourcePropertyPath"));
				HikariDataSource eventDataSource = new HikariDataSource(eventDataSourceConfig);
				add(eventDataSource);

				MachineIdGenerator machineIdGenerator = new MachineIdGenerator(attributes.getString("workerId"), attributes.getString("datacenterId"));
				add(machineIdGenerator);

				add(attributes.getString("eventDir"));
				add(attributes.getNumber("fileEventCount"));
				add(attributes.getNumber("eventThreads"));
			}
		});

		// register commandBus
		BeanRegistrationUtil.registerBeanDefinitionWithPropertyReferencesIfNotExists(registry, CommandBus.class.getName(), SimpleCommandBus.class, new HashMap<String, String>() {
			{
				put("dbContext", DbContext.class.getName());
			}
		});
		
		// register eventBus
		BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, EventBus.class.getName(), SimpleEventBus.class);

		BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, CommandBusPostProcessor.class.getName(), CommandBusPostProcessor.class);
		BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, EventBusPostProcessor.class.getName(), EventBusPostProcessor.class);
	}

}
