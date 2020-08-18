package com.sundy.lingbao.core.cqrs.message.command;

import com.sundy.lingbao.core.cqrs.annotations.aggregate.TargetAggregateIdentify;
import com.sundy.lingbao.core.util.ClassUtil;

public class GenericCommandMessage<T> implements CommandMessage<T> {

	private static final long serialVersionUID = -1317545366487893746L;
	
	private String aggregateIdentifier;
	
	private T payload;
	
	private Class<?> payloadType;
	
	private String commandName;
	
	public GenericCommandMessage(T payload) {
		this.payload = payload;
		this.payloadType = payload.getClass();
		this.commandName = payload.getClass().getName();
		Object identifierObject = ClassUtil.getFieldValueByAnnotation(payload, TargetAggregateIdentify.class);
		this.aggregateIdentifier = identifierObject==null ? null:identifierObject.toString();
	}
	
	@Override
	public String getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	@Override
	public T getPayload() {
		return payload;
	}

	@Override
	public Class<?> getPayloadType() {
		return payloadType;
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

}
