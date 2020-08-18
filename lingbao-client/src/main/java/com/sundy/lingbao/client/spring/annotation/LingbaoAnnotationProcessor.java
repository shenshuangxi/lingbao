package com.sundy.lingbao.client.spring.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import com.sundy.lingbao.client.ConfigService;
import com.sundy.lingbao.client.config.Config;
import com.sundy.lingbao.client.listeners.ConfigChangeListener;
import com.sundy.lingbao.core.util.ClassUtil;

public class LingbaoAnnotationProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		processMethod(bean, bean.getClass());
		return bean;
	}

	private void processMethod(Object bean, Class<?> clazz) {
		List<Method> methods = ClassUtil.findAllMethods(clazz);
		for (Method method : methods) {
			if (method.isAnnotationPresent(LingbaoConfigChangeListener.class)) {
			      if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()) || Modifier.isFinal(method.getModifiers())) && !method.isAccessible()) {
			    	  method.setAccessible(true);
			      }
			      int order = method.getAnnotation(LingbaoConfigChangeListener.class).ordered();
			      Config config = ConfigService.getConfig();
		    	  config.register(new ConfigChangeListener() {
		    		  @Override
		    		  public void onConfigChange() {
		    			  ReflectionUtils.invokeMethod(method, bean);
		    		  }
		    		  @Override
		    		  public int getOrdered() {
						return order;
		    		  }
		    	  });
			}
		}
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	
	
}
