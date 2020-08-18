package com.sundy.lingbao.cqrs.command.queue;

import com.sundy.lingbao.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.cqrs.message.CommandMessage;

public interface CommandMessageQueue {

	
	<C, R> void registerCommandMessage(CommandMessage<C> commandMessage, CommandCallback<C, R> callback);
	
}
