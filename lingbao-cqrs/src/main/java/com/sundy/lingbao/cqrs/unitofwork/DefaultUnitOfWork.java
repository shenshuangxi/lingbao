package com.sundy.lingbao.cqrs.unitofwork;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.cqrs.aggregate.AggregateRoot;
import com.sundy.lingbao.cqrs.message.GenericEventMessage;
import com.sundy.lingbao.cqrs.message.Message;

public class DefaultUnitOfWork<M extends Message<?>> implements UnitOfWork<M> {

	private final M message;
	
	private AggregateRoot<?> aggregate;
	
	private Deque<Object> events = new LinkedList<Object>();
	
	public DefaultUnitOfWork(M message) {
		this.message = message;
	}
	
	public M getMessage() {
		return message;
	}

	public static <M extends Message<?>> UnitOfWork<M> createAndStart(M message){
		DefaultUnitOfWork<M> defaultUnitOfWork = new DefaultUnitOfWork<M>(message);
		defaultUnitOfWork.start();
		return defaultUnitOfWork;
	}

	@Override
	public void start() {
		CurrentUnitOfWork.set(this);
	}
	
	@Override
	public void commit() {
		if (aggregate!=null) {
			while(!events.isEmpty()) {
				Object eventPayload = events.pop();
				GenericEventMessage<?> eventMessage = new GenericEventMessage<Object>(eventPayload);
				this.aggregate.publish(eventMessage);
			}
			CurrentUnitOfWork.clear(this);
		} else {
			throw new LingbaoException(-1, "aggregate not exists in unit of work");
		}
	}
	
	@Override
	public void rollback(Throwable cause) {
		CurrentUnitOfWork.clear(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R execute(Callable<R> task) {
		try {
			R result = task.call();
			commit();
			if (result==this.aggregate) {
				return (R) this.aggregate.aggregateIdentifier();
			} else {
				return result;
			}
		} catch (Throwable cause) {
			rollback(cause);
		}
		return null;
	}
	
	@Override
	public void setAggregate(AggregateRoot<?> aggregate) {
		this.aggregate = aggregate;
	}
	
	@Override
	public AggregateRoot<?> getAggregate() {
		return this.aggregate;
	}

	@Override
	public void registerEvent(Object payload) {
		this.events.push(payload);
	}

}
