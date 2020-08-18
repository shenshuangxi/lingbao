package com.sundy.db.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.db.event.annotation.EventHandler;
import com.sundy.lingbao.core.util.ClassUtil;

public class AnnotationEventListenerAdapter implements EventListener {

	private final Logger logger = LoggerFactory.getLogger(AnnotationEventListenerAdapter.class);
	
	private Map<String, Method> eventListeners = new HashMap<String, Method>();
	private final Object eventListener;
	
	public AnnotationEventListenerAdapter(Object eventListener, EventBus eventBus) {
		this.eventListener = eventListener;
		List<Method> methods = ClassUtil.findAllMethods(eventListener.getClass());
		for (Method method : methods) {
			if (method.isAnnotationPresent(EventHandler.class)) {
				String eventName = method.getParameterTypes()[0].getName();
				eventListeners.put(eventName, method);
				eventBus.registerListener(eventName, this);
			}
		}
	}

	@Override
	public void handle(Object event) {
		try {
			Method method = eventListeners.get(event.getClass().getName());
			method.invoke(eventListener, event);
		} catch (Exception e) {
			logger.error(String.format("execute [%s] event in [%s] listener", event.getClass().getSimpleName(), eventListener.getClass().getSimpleName()), e);
		}
	}
	

}
