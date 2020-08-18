package com.sundy.lingbao.cqrs.command.bus;

import com.sundy.lingbao.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.cqrs.command.handler.CommandHandler;
import com.sundy.lingbao.cqrs.message.CommandMessage;

public interface CommandBus {

	<C, R> void dispatch(CommandMessage<C> commandMessage, CommandCallback<C, R> callback);
	
	void subscribe(String commandName, CommandHandler<CommandMessage<?>> commandHandler);
	
	void unSubscribe(String commandName, CommandHandler<CommandMessage<?>> commandHandler);
	
}
