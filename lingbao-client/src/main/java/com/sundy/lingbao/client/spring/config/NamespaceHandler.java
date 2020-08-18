package com.sundy.lingbao.client.spring.config;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.sundy.lingbao.client.ConfigService;
import com.sundy.lingbao.client.spring.annotation.LingbaoAnnotationProcessor;
import com.sundy.lingbao.client.spring.annotation.LingbaoApplicationRestart;
import com.sundy.lingbao.client.spring.annotation.LingbaoConfigPropertiesRefresh;
import com.sundy.lingbao.client.spring.boot.EnviromentPropertyBeanFactoryPostProcessor;
import com.sundy.lingbao.client.spring.util.BeanRegistrationUtil;
import com.sundy.lingbao.core.util.StringUtils;

public class NamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new ConfigParser());
	}
	
	public static class ConfigParser extends AbstractSingleBeanDefinitionParser {

		@Override
		protected Class<?> getBeanClass(Element element) {
			return EnviromentPropertyBeanFactoryPostProcessor.class;
		}
		
		@Override
		protected boolean shouldGenerateId() {
			return true;
		}
		
		@Override
		protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
			super.registerBeanDefinition(definition, registry);
			BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, LingbaoAnnotationProcessor.class.getName(), LingbaoAnnotationProcessor.class, null);
		}
		
		

		@Override
		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			super.doParse(element, parserContext, builder);
			
			String appId = element.getAttribute("appId");
			if (!StringUtils.isNullOrEmpty(appId)) {
				ConfigService.setAppId(appId);
		    }
			String registerServerHost = element.getAttribute("registerServerHost");
			if (!StringUtils.isNullOrEmpty(registerServerHost)) {
				ConfigService.setRegisterServerHost(registerServerHost);
		    }
			
			String enableRestart = element.getAttribute("enableRestart");
			if (!StringUtils.isNullOrEmpty(enableRestart)) {
				if (Boolean.valueOf(enableRestart)) {
					BeanRegistrationUtil.registerBeanDefinitionIfNotExists(parserContext.getRegistry(), LingbaoApplicationRestart.class.getName(), LingbaoApplicationRestart.class, null);
				}
		    }
			
			String enableRefresh = element.getAttribute("enableRefresh");
			if (!StringUtils.isNullOrEmpty(enableRefresh)) {
				if (Boolean.valueOf(enableRefresh)) {
					BeanRegistrationUtil.registerBeanDefinitionIfNotExists(parserContext.getRegistry(), LingbaoConfigPropertiesRefresh.class.getName(), LingbaoConfigPropertiesRefresh.class, null);
				}
		    }
		}

	
		
	}

}
