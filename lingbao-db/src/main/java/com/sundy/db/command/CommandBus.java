package com.sundy.db.command;

import com.sundy.db.constant.DbConstant.UnitOfWorkType;

public interface CommandBus {

	@SuppressWarnings("rawtypes")
	void subscribe(String handlerName, CommandHandler handler);
	 
	@SuppressWarnings("rawtypes")
	boolean unsubscribe(String handlerName, CommandHandler handler);

	<C, R> R dispatch(C command, UnitOfWorkType unitOfWorkType) throws Throwable;
	
}
