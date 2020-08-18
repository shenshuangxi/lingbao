package com.sundy.lingbao.cqrs.command.callback;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.cqrs.message.CommandMessage;

public class LoggerCommandCallback implements CommandCallback<Object, Object> {

	private final static Logger logger = LoggerFactory.getLogger(LoggerCommandCallback.class);
	
	public static final LoggerCommandCallback INSTANCE = new LoggerCommandCallback();
	
	private LoggerCommandCallback() {}
	
	@Override
	public void onSuccess(CommandMessage<Object> commandMessage, Object result) {
		logger.info("Command executed successfully: {}", commandMessage.getCommandName());
	}

	@Override
	public void onFail(CommandMessage<Object> commandMessage, Throwable cause) {
		logger.warn("Command resulted in exception: {}", commandMessage.getCommandName(), cause);
		
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getResult(long timeout, TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandMessage<Object> getCommandMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
