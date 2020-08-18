package com.sundy.lingbao.core.cqrs.command;

import com.sundy.lingbao.core.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.core.cqrs.command.callback.LoggerCommandCallback;
import com.sundy.lingbao.core.cqrs.command.handler.CommandMessageHandler;
import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;

public interface CommandBus {

	default <C> void dispatch(CommandMessage<? extends C> commandMessage){
		dispatch(commandMessage, LoggerCommandCallback.INSTANCE);
	}
	
	<C> void dispatch(CommandMessage<? extends C> commandMessage, CommandCallback<C, Object> commandCallback);
	
	
	void register(String commandName, CommandMessageHandler<? extends CommandMessage<?>> handler);

}
