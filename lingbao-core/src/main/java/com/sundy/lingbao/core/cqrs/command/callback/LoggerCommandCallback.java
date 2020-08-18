package com.sundy.lingbao.core.cqrs.command.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.cqrs.message.command.CommandMessage;

public class LoggerCommandCallback implements CommandCallback<Object, Object> {

	private final static Logger logger = LoggerFactory.getLogger(LoggerCommandCallback.class);

	public static final LoggerCommandCallback INSTANCE = new LoggerCommandCallback();

	private LoggerCommandCallback() {}

	@Override
	public void success(CommandMessage<? extends Object> commandMessage, Object result) {
		logger.info("Command executed successfully: {}", commandMessage.getCommandName());
		
	}

	@Override
	public void fail(CommandMessage<? extends Object> commandMessage, Throwable cause) {
		logger.warn("Command resulted in exception: {}", commandMessage.getCommandName(), cause);

	}

}
