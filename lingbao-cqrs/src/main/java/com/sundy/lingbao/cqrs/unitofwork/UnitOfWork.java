package com.sundy.lingbao.cqrs.unitofwork;

import java.util.concurrent.Callable;

import com.sundy.lingbao.cqrs.aggregate.AggregateRoot;
import com.sundy.lingbao.cqrs.message.Message;

public interface UnitOfWork<M extends Message<?>> {

	public void start();
	
	public void commit();
	
	public void rollback(Throwable cause);
	
	public M getMessage();
	
	public <R> R execute(Callable<R> task);
	
	public void setAggregate(AggregateRoot<?> aggregate);
	
	public AggregateRoot<?> getAggregate();

	public void registerEvent(Object payload);

}
