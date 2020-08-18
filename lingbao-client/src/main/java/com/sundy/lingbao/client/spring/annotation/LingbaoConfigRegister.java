package com.sundy.lingbao.client.spring.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.sundy.lingbao.client.ConfigService;
import com.sundy.lingbao.client.spring.boot.EnviromentPropertyBeanFactoryPostProcessor;
import com.sundy.lingbao.client.spring.util.BeanRegistrationUtil;

public class LingbaoConfigRegister implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableLingbaoConfig.class.getName()));
	    ConfigService.setAppId(attributes.getString("appId"));
	    ConfigService.setRegisterServerHost(attributes.getString("registerServerHost"));
	    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, EnviromentPropertyBeanFactoryPostProcessor.class.getName(), EnviromentPropertyBeanFactoryPostProcessor.class, null);
	    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, LingbaoAnnotationProcessor.class.getName(), LingbaoAnnotationProcessor.class, null);
	}

}
