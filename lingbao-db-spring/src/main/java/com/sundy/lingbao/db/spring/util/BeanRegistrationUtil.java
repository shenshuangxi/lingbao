package com.sundy.lingbao.db.spring.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class BeanRegistrationUtil {

	public static boolean registerBeanDefinitionIfNotExists(
			BeanDefinitionRegistry registry, 
			String beanName, Class<?> beanClass, 
			Map<String, Object> propertyValues, 
			Map<String, String> propertyReferences, 
			List<Object> constuctValues, 
			List<String> constuctReferences) {
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
		
		if (propertyReferences!=null && propertyReferences.size()>0) {
			for (Entry<String, String> entry : propertyReferences.entrySet()) {
				beanDefinitionBuilder.addPropertyReference(entry.getKey(), entry.getValue());
			}
		}
		
		if (constuctValues!=null && constuctValues.size()>0) {
			for (Object obj : constuctValues) {
				beanDefinitionBuilder.addConstructorArgValue(obj);
			}
		}
		
		if (constuctReferences!=null && constuctReferences.size()>0) {
			for (String constuctReference : constuctReferences) {
				beanDefinitionBuilder.addConstructorArgReference(constuctReference);
			}
		}
		beanDefinitionBuilder.setScope("singleton");
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		registry.registerBeanDefinition(beanName, beanDefinition);
		return true;
	}
	
	public static boolean registerBeanDefinitionWithConstructValuesIfNotExists(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, List<Object> constuctValues) {
		return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, null, null, constuctValues, null);
	}
	
	public static boolean registerBeanDefinitionWithConstructReferencesIfNotExists(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, List<String> constuctReferences) {
		return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, null, null, null, constuctReferences);
	}
	
	public static boolean registerBeanDefinitionWithPropertyValuesIfNotExists(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, Map<String, Object> propertyValues) {
		return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, propertyValues, null, null, null);
	}
	
	public static boolean registerBeanDefinitionWithPropertyReferencesIfNotExists(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass, Map<String, String> propertyReferences) {
		return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, null, propertyReferences, null, null);
	}
	
	public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass) {
		return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, null, null, null, null);
	}
	
}
