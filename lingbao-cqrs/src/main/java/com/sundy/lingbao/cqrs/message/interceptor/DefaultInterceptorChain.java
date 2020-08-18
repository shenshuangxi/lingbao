package com.sundy.lingbao.cqrs.message.interceptor;

import java.util.Iterator;

import com.sundy.lingbao.cqrs.command.handler.CommandHandler;
import com.sundy.lingbao.cqrs.message.Message;
import com.sundy.lingbao.cqrs.unitofwork.UnitOfWork;

public class DefaultInterceptorChain<C extends Message<?>> implements InterceptorChain {

	private final UnitOfWork<C> unitOfWork;
	
	private final Iterator<MessageHandlerInterceptor<C>> chain;
	
	private final CommandHandler<C> commandHandler;
	
	public DefaultInterceptorChain(UnitOfWork<C> unitOfWork, Iterator<MessageHandlerInterceptor<C>> chain, CommandHandler<C> commandHandler) {
		this.unitOfWork = unitOfWork;
		this.chain = chain;
		this.commandHandler = commandHandler;
	}

	@Override
	public Object proceed() throws Exception {
		if (chain.hasNext()) {
			return chain.next().handle(unitOfWork, this);
		} else {
			return commandHandler.handle(unitOfWork.getMessage(), unitOfWork);
		}
	}

}
