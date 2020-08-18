package com.sundy.lingbao.cqrs.event.listeners;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.cqrs.annotations.aggregate.Aggregate;
import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.core.util.ClassUtil;
import com.sundy.lingbao.cqrs.aggregate.annotation.EventHandler;
import com.sundy.lingbao.cqrs.event.bus.EventBus;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.message.handler.MessageHandler;

public class EventListenerAdapter implements EventListener<EventMessage<?>> {

	private final Logger logger = LoggerFactory.getLogger(EventListenerAdapter.class);
	
	private Map<String, MessageHandler<EventMessage<?>>> eventListeners = new HashMap<String, MessageHandler<EventMessage<?>>>();
	private final Object targetListener;
	
	public EventListenerAdapter(Object targetListener, EventBus eventBus) {
		if (!targetListener.getClass().isAnnotationPresent(Aggregate.class)) {
			List<Method> methods = ClassUtil.findAllMethods(targetListener.getClass());
			for (Method method : methods) {
				if (method.isAnnotationPresent(EventHandler.class)) {
					eventListeners.put(method.getParameterTypes()[0].getName(), new AnnotatedEventMessageHandler(method));
				}
			}
		}
		this.targetListener = targetListener;
		eventBus.registerListener(this);
	}
	
	@Override
	public void handle(EventMessage<?> eventMessage) {
		MessageHandler<EventMessage<?>> messageHandler = eventListeners.get(eventMessage.getPayloadType());
		if (messageHandler!=null) {
			messageHandler.handle(eventMessage);
		}
	}
	
	private class AnnotatedEventMessageHandler implements MessageHandler<EventMessage<?>> {
		private final Method method;
		public AnnotatedEventMessageHandler(Method method) {
			this.method = method;
		}
		
		@Override
		public boolean match(EventMessage<?> message) {
			return this.method.getParameterTypes()[0].getName().equals(message.getPayloadType());
		}

		@Override
		public Object handle(EventMessage<?> eventMessage) {
			try {
				Object ret = method.invoke(targetListener, eventMessage.getPayload());
				return ret;
			} catch (Throwable e) {
				logger.error("operate event fail", e);
				throw new LingbaoException(-1, "operate event fail");
			}
		}
	}

}
