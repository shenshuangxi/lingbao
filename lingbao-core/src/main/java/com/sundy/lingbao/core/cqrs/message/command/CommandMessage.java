package com.sundy.lingbao.core.cqrs.message.command;

import com.sundy.lingbao.core.cqrs.message.Message;

public interface CommandMessage<T> extends Message<T> {

	String getCommandName();
	
}
