package com.sundy.lingbao.core.cqrs.command;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.core.cqrs.command.handler.CommandMessageHandler;
import com.sundy.lingbao.core.cqrs.exception.LingbaoCqrsException;
import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;


public class SimpleCommandBus implements CommandBus {

	private static final Logger logger = LoggerFactory.getLogger(SimpleCommandBus.class);
	
	private final ConcurrentMap<String, CommandMessageHandler<? extends CommandMessage<?>>> registerCommandHandler = new ConcurrentHashMap<>();
	
	@Override
	public <C> void dispatch(CommandMessage<? extends C> commandMessage, CommandCallback<C, Object> commandCallback) {
		CommandMessageHandler<? extends CommandMessage<?>> commandMessageHandler = registerCommandHandler.get(commandMessage.getCommandName());
		try {
			if (Objects.nonNull(commandMessageHandler)) {
				Object result = commandMessageHandler.handlerMessage(commandMessage);
				commandCallback.success(commandMessage, result);
			} else {
				throw new LingbaoCqrsException("not found handler for " + commandMessage.getCommandName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			commandCallback.fail(commandMessage, e);
		}
	}

	@Override
	public void register(String commandName, CommandMessageHandler<? extends CommandMessage<?>> handler) {
		registerCommandHandler.put(commandName, handler);
	}

}
