package com.sundy.lingbao.cqrs.command.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.cqrs.command.callback.CommandCallback;
import com.sundy.lingbao.cqrs.command.handler.CommandHandler;
import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.message.interceptor.DefaultInterceptorChain;
import com.sundy.lingbao.cqrs.message.interceptor.InterceptorChain;
import com.sundy.lingbao.cqrs.message.interceptor.MessageHandlerInterceptor;
import com.sundy.lingbao.cqrs.unitofwork.DefaultUnitOfWork;
import com.sundy.lingbao.cqrs.unitofwork.UnitOfWork;

public class MainCommandBus implements CommandBus {

	private Map<String, CommandHandler<CommandMessage<?>>> subscriptions = new HashMap<String, CommandHandler<CommandMessage<?>>>();
	private List<MessageHandlerInterceptor<CommandMessage<?>>> handlerInterceptors = new ArrayList<>();
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <C, R> void dispatch(CommandMessage<C> commandMessage, CommandCallback<C, R> callback) {
		CommandHandler<CommandMessage<?>> commandHandler = subscriptions.get(commandMessage.getCommandName());
		if(commandHandler==null){
			throw new LingbaoException(-1, String.format("No handler was subscribed to command [%s]", commandMessage.getCommandName()));
		}
		try {
			UnitOfWork<CommandMessage<?>> unitOfWork = DefaultUnitOfWork.createAndStart(commandMessage);
			InterceptorChain interceptorChain = new DefaultInterceptorChain<CommandMessage<?>>(unitOfWork, handlerInterceptors.iterator(), commandHandler);
			R result = (R) unitOfWork.execute(interceptorChain::proceed);
			callback.onSuccess(commandMessage, result);
		} catch (Throwable cause) {
			callback.onFail(commandMessage, cause);
		}
	}

	@Override
	public void subscribe(String commandName, CommandHandler<CommandMessage<?>> commandHandler) {
		subscriptions.put(commandName, commandHandler);
	}

	@Override
	public void unSubscribe(String commandName, CommandHandler<CommandMessage<?>> commandHandler) {
		subscriptions.remove(commandName, commandHandler);
	}

}
