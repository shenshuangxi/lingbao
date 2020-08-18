package com.sundy.lingbao.cqrs.message;

import com.sundy.lingbao.core.idgenerate.IdGenerateFactory;

@SuppressWarnings("serial")
public class GenericCommandMessage<T> implements CommandMessage<T> {

	private String identifier;
	
	private String commandName;
	
	private T payload;
	
	private String pyaloadType;
	
	public GenericCommandMessage(T payload) {
		this.payload = payload;
		this.identifier = IdGenerateFactory.getIdGenerator().getId();
		this.commandName = payload.getClass().getName();
		this.pyaloadType = payload.getClass().getName();;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public T getPayload() {
		return payload;
	}

	@Override
	public String getPayloadType() {
		return pyaloadType;
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

}
