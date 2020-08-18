package com.sundy.lingbao.core.cqrs.command.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sundy.lingbao.core.cqrs.annotations.aggregate.CommandHandler;
import com.sundy.lingbao.core.cqrs.command.CommandBus;
import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;
import com.sundy.lingbao.core.cqrs.repository.Repository;
import com.sundy.lingbao.core.util.ClassUtil;

public class AggregateAnnotationMessageHandler<T> implements CommandMessageHandler<CommandMessage<?>> {

	private Repository repository;
	
	private final Map<String, CommandMessageHandler<CommandMessage<?>>> handlers;
	
	public AggregateAnnotationMessageHandler(Class<?> aggregateType) {
		this.handlers = initializeHandlers(aggregateType);
	}
	

	public void register(CommandBus commandBus) {
		if (Objects.nonNull(handlers)) {
			for (String commandName : handlers.keySet()) {
				commandBus.register(commandName, this);
			}
		}
	}

	@Override
	public Object handlerMessage(CommandMessage<?> commandMessage) {
		return handlers.get(commandMessage.getCommandName()).handlerMessage(commandMessage);
	}
	
	
	private Map<String, CommandMessageHandler<CommandMessage<?>>> initializeHandlers(Class<?> aggregateType) {
		Map<String, CommandMessageHandler<CommandMessage<?>>> handlers = new HashMap<String, CommandMessageHandler<CommandMessage<?>>>();
		List<Method> methods = ClassUtil.findMethodsByAnnotation(aggregateType, CommandHandler.class);
		if (Objects.nonNull(methods)) {
			for (Method method : methods) {
				String key = method.getParameterTypes()[0].getClass().getName();
				handlers.put(key, new GenericCommandMessageHandlerAdapter(method, repository));
			}
		}
		Constructor<?>[] constructors = aggregateType.getConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.isAnnotationPresent(CommandHandler.class)) {
				String key = constructor.getParameterTypes()[0].getClass().getName();
				handlers.put(key, new GenericCommandMessageHandlerAdapter(constructor,constructor.getParameterTypes()[0].getClass(), repository));
			}
		}
		return handlers;
	}
}
