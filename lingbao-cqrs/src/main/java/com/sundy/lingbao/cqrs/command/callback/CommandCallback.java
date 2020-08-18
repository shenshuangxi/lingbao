package com.sundy.lingbao.cqrs.command.callback;

import java.util.concurrent.TimeUnit;

import com.sundy.lingbao.cqrs.message.CommandMessage;

public interface CommandCallback<C,R> {

	void onSuccess(CommandMessage<C> commandMessage, R result);
	
	void onFail(CommandMessage<C> commandMessage, Throwable cause);
	
	R getResult();
	
	R getResult(long timeout, TimeUnit timeUnit);
	
	CommandMessage<C> getCommandMessage();
	
}
