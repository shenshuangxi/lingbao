package com.sundy.lingbao.client.spring.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;


public class BeanRegistrationUtil {

	public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, Map<String, Object> propertyValues) {
		
		if (registry.containsBeanDefinition(beanName)) {
			return false;
		}
		
		String[] candidates = registry.getBeanDefinitionNames();
		for (String candidate : candidates) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
			if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
				return false;
			}
		}
		
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
		if (propertyValues!=null && propertyValues.size()>0) {
			for (Entry<String, Object> entry : propertyValues.entrySet()) {
				beanDefinitionBuilder.addPropertyValue(entry.getKey(), entry.getValue());
			}
		}
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		registry.registerBeanDefinition(beanName, beanDefinition);
		return true;
	}

}
