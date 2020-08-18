package com.sundy.lingbao.cqrs.message;

public interface CommandMessage<T> extends Message<T> {

	String getCommandName();
	
}
