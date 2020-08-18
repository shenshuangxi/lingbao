package com.sundy.lingbao.cqrs.command.gateway;

import java.util.concurrent.TimeUnit;

import com.sundy.lingbao.cqrs.command.bus.CommandBus;
import com.sundy.lingbao.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.cqrs.command.callback.FutureCommandCallback;
import com.sundy.lingbao.cqrs.command.callback.LoggerCommandCallback;
import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.message.GenericCommandMessage;

public class SimpleCommandGateway implements CommandGateway {

	private CommandBus commandBus;
	
	public SimpleCommandGateway(CommandBus commandBus) {
		this.commandBus = commandBus;
	}

	@Override
	public <C, R> R sendAndWait(C command) {
		FutureCommandCallback<C, R> futureCommandCallback = new FutureCommandCallback<C, R>();
		send(command, futureCommandCallback);
		return futureCommandCallback.getResult();
	}

	@Override
	public <C, R> R sendAndWait(C command, Long timeout, TimeUnit timeUnit) {
		FutureCommandCallback<C, R> futureCommandCallback = new FutureCommandCallback<C, R>();
		send(command, futureCommandCallback);
		return futureCommandCallback.getResult(timeout, timeUnit);
	}
	
	@Override
	public <C> void send(C command) {
		send(command, LoggerCommandCallback.INSTANCE);
	}

	public <C, R> void send(C command, CommandCallback<C, R> callback) {
		CommandMessage<C> commandMessage = new GenericCommandMessage<C>(command);
		commandBus.dispatch(commandMessage, callback);
	}

}
