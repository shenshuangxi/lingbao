package com.sundy.lingbao.cqrs.message.interceptor;

import com.sundy.lingbao.cqrs.message.Message;
import com.sundy.lingbao.cqrs.unitofwork.UnitOfWork;

public interface MessageHandlerInterceptor<M extends Message<?>> {

	public Object  handle(UnitOfWork<M> unitOfWork, InterceptorChain chain);
	
}
