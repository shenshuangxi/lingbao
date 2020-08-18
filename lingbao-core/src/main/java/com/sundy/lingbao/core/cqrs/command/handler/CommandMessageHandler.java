package com.sundy.lingbao.core.cqrs.command.handler;

import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;

public interface CommandMessageHandler<C extends CommandMessage<?>>  {

	Object handlerMessage(CommandMessage<?> message);
	
}
