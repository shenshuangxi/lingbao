package com.sundy.db.command;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sundy.db.DbContext;
import com.sundy.db.DbException;
import com.sundy.db.constant.DbConstant.UnitOfWorkType;
import com.sundy.db.unitofwork.DefaultUnitOfWorkFactory;
import com.sundy.db.unitofwork.UnitOfWork;
import com.sundy.db.unitofwork.UnitOfWorkFactory;

public class SimpleCommandBus implements CommandBus {

	@SuppressWarnings("rawtypes")
	private final ConcurrentMap<String, CommandHandler> subscriptions = new ConcurrentHashMap<String, CommandHandler>();
	private final UnitOfWorkFactory unitOfWorkFactory = new DefaultUnitOfWorkFactory();
	
	private DbContext dbContext;
	
	public void setDbContext(DbContext dbContext) {
		this.dbContext = dbContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C, R> R dispatch(final C command, UnitOfWorkType unitOfWorkType) throws Throwable {
		final CommandHandler<C> handler = subscriptions.get(command.getClass().getName());
		if(handler == null){
			throw new DbException(String.format("No handler was subscribed to command [%s]",command.getClass().getName()));
		}
		UnitOfWork unitOfWork = unitOfWorkFactory.createUnitOfWork(unitOfWorkType, dbContext);
		R returnValue = unitOfWork.execute(new Callable<R>() {
				@Override
				public R call() throws Exception {
					return (R) handler.handle(command);
				}
			});
		return returnValue;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public  void subscribe(String handlerName, CommandHandler handler) {
		subscriptions.put(handlerName, handler);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public  boolean unsubscribe(String handlerName, CommandHandler handler) {
		return subscriptions.remove(handlerName, handler);
	}



}
