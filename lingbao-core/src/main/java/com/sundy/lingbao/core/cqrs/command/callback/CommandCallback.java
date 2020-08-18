package com.sundy.lingbao.core.cqrs.command.callback;

import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;

public interface CommandCallback<C, R> {

	void success(CommandMessage<? extends C> commandMessage, Object result);
	
	void fail(CommandMessage<? extends C> commandMessage, Throwable cause);
	
}
