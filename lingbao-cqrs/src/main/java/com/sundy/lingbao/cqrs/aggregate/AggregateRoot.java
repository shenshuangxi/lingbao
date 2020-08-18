package com.sundy.lingbao.cqrs.aggregate;

import java.util.Collection;

import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.unitofwork.CurrentUnitOfWork;

/**
 * 
 * @author Administrator
 *
 * @param <T> the aggreage type name
 */
public interface AggregateRoot<T> {

	String getAggregateType();
	
	T getAggregatePayload();
	
	String aggregateIdentifier();
	
	Long getAggregateVersion();
	
	
	Collection<String> getSupportCommands();
	
	Object handle(CommandMessage<?> commandMessage);
	
	Object handle(EventMessage<?> eventMessage);
	
	void publish(EventMessage<?> eventMessage);
	
	static void dispatchEvent(Object eventPayload) {
		CurrentUnitOfWork.get().registerEvent(eventPayload);
	}
	
}
