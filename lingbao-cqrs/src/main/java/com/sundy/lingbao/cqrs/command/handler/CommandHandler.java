package com.sundy.lingbao.cqrs.command.handler;

import com.sundy.lingbao.cqrs.message.Message;
import com.sundy.lingbao.cqrs.unitofwork.UnitOfWork;

public interface CommandHandler<M extends Message<?>> {

	Object handle(M message, UnitOfWork<M> unitOfWork);
	
}
