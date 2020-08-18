package com.sundy.lingbao.core.cqrs.event;

import com.sundy.lingbao.core.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.core.cqrs.command.callback.LoggerCommandCallback;
import com.sundy.lingbao.core.cqrs.event.handler.EventMessageHandler;
import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;
import com.sundy.lingbao.core.cqrs.message.event.EventMessage;

public interface EventBus {

	default <C> void dispatch(CommandMessage<? extends C> commandMessage){
		dispatch(commandMessage, LoggerCommandCallback.INSTANCE);
	}
	
	<C, R> void dispatch(CommandMessage<? extends C> commandMessage, CommandCallback<C, R> commandCallback);
	
	
	void register(String commandName, EventMessageHandler<? extends EventMessage<?>> handler);
	
}
