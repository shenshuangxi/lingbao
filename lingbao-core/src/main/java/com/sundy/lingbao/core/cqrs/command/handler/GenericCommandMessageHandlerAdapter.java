package com.sundy.lingbao.core.cqrs.command.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.cqrs.annotations.aggregate.AggregateIdentifier;
import com.sundy.lingbao.core.cqrs.exception.LingbaoCqrsException;
import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;
import com.sundy.lingbao.core.cqrs.repository.Repository;
import com.sundy.lingbao.core.util.ClassUtil;

public class GenericCommandMessageHandlerAdapter implements CommandMessageHandler<CommandMessage<?>> {

	private final static Logger logger = LoggerFactory.getLogger(GenericCommandMessageHandlerAdapter.class);
	
	private final Method method;
	
	private final Constructor<?> constructor;
	
	private final Class<?> payloadType;
	
	private final Repository repository;
	
	public GenericCommandMessageHandlerAdapter(Method method, Repository repository) {
		this.method = method;
		this.repository = repository;
		this.constructor = null;
		this.payloadType = null;
	}
	
	public GenericCommandMessageHandlerAdapter(Constructor<?> constructor, Class<?> payloadType, Repository repository) {
		this.method = null;
		this.repository = repository;
		this.constructor = constructor;
		this.payloadType = payloadType;
	}

	@Override
	public Object handlerMessage(CommandMessage<?> commandMessage) {
		Throwable error = null;
		try {
			String identifier = commandMessage.getAggregateIdentifier();
			if (Objects.nonNull(identifier)) {
				Object targetObject = repository.loadAggregate(identifier);
				if (Objects.isNull(targetObject) && Objects.nonNull(constructor) && commandMessage.getPayloadType().equals(payloadType)) {
					targetObject = constructor.newInstance(commandMessage.getPayload());
					repository.saveAggregate(targetObject);
					return ClassUtil.getFieldValueByAnnotation(targetObject, AggregateIdentifier.class);
				} else if (Objects.nonNull(targetObject) && Objects.nonNull(method)) {
					Object retVal = method.invoke(targetObject, commandMessage);
					repository.saveAggregate(targetObject);
					return retVal;
				} else if (Objects.nonNull(targetObject) && Objects.nonNull(constructor) && commandMessage.getPayloadType().equals(payloadType)) {
					logger.error(String.format("the aggregate of identify[%s] is exists", identifier));
					throw new LingbaoCqrsException(String.format("the aggregate of identify[%s] is exists", identifier));
				}
			} else if(Objects.isNull(identifier)) {
				if (commandMessage.getPayloadType().equals(payloadType)) {
					Object targetObject = constructor.newInstance(commandMessage.getPayload());
					repository.saveAggregate(targetObject);
					return ClassUtil.getFieldValueByAnnotation(targetObject, AggregateIdentifier.class);
				}
			}
		} catch (Throwable cause) {
			error = cause;
		} 
		logger.error("not found the aggregate");
		if (Objects.nonNull(error)) {
			throw new LingbaoCqrsException("not found the aggregate",error);
		} else {
			throw new LingbaoCqrsException("not found the aggregate");
		}
		
	}
	
}
