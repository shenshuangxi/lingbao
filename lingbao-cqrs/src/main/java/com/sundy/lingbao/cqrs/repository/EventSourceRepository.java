package com.sundy.lingbao.cqrs.repository;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.cqrs.aggregate.AggregateRoot;
import com.sundy.lingbao.cqrs.aggregate.AnnotatedAggregate;
import com.sundy.lingbao.cqrs.event.bus.EventBus;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.message.EventStream;
import com.sundy.lingbao.cqrs.store.EventStore;
import com.sundy.lingbao.cqrs.unitofwork.CurrentUnitOfWork;

public class EventSourceRepository<T> implements AggregateRepository<T> {

	private final static Logger logger = LoggerFactory.getLogger(EventSourceRepository.class);
	
	private final EventStore eventStore;
	
	private final EventBus eventBus;
	
	public EventSourceRepository(EventStore eventStore, EventBus eventBus) {
		this.eventStore = eventStore;
		this.eventBus = eventBus;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregateRoot<T> load(String aggrgateType, String aggregrateIdentifier, Long version) {
		EventStream eventStream = eventStore.readEvents(aggrgateType, aggregrateIdentifier, version);
		AggregateRoot<T> aggregate = null;
		try {
			EventMessage<?> eventMessage = eventStream.next();
			aggregate = new AnnotatedAggregate<T>(this, (T)Class.forName(eventMessage.getAggregateType()).newInstance());
			CurrentUnitOfWork.get().setAggregate(aggregate);
			aggregate.handle(eventMessage);
			while(eventStream.hasNext()) {
				aggregate.handle(eventStream.next());
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new LingbaoException(-1, "load aggregate fail");
		}
		return aggregate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregateRoot<T> load(String aggrgateType, String aggregrateIdentifier) {
		EventStream eventStream = eventStore.readEvents(aggrgateType, aggregrateIdentifier);
		AggregateRoot<T> aggregate = null;
		try {
			EventMessage<?> eventMessage = eventStream.next();
			aggregate = new AnnotatedAggregate<T>(this, (T)Class.forName(eventMessage.getAggregateType()).newInstance());
			CurrentUnitOfWork.get().setAggregate(aggregate);
			aggregate.handle(eventMessage);
			while(eventStream.hasNext()) {
				aggregate.handle(eventStream.next());
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new LingbaoException(-1, "load aggregate fail");
		}
		return aggregate;
	}

	@Override
	public AggregateRoot<T> newInstance(Callable<T> factoryMethod) {
		AggregateRoot<T> aggregate = null;
		try {
			T target = factoryMethod.call();
			aggregate = new AnnotatedAggregate<T>(this, target);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new LingbaoException(-1, "crate aggregate fail");
		}
		return aggregate;
	}

	@Override
	public EventStore getEventStore() {
		return this.eventStore;
	}

	@Override
	public EventBus getEventBus() {
		return this.eventBus;
	}

}
