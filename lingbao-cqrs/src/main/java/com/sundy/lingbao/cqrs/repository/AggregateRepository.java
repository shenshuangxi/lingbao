package com.sundy.lingbao.cqrs.repository;

import java.util.concurrent.Callable;

import com.sundy.lingbao.cqrs.aggregate.AggregateRoot;
import com.sundy.lingbao.cqrs.event.bus.EventBus;
import com.sundy.lingbao.cqrs.store.EventStore;

public interface AggregateRepository<T> {

	AggregateRoot<T> load(String aggrgateType, String aggregrateIdentifier, Long version);
	
	AggregateRoot<T> load(String aggrgateType, String aggregrateIdentifier);
	
	AggregateRoot<T> newInstance(Callable<T> factoryMethod);
	
	EventStore getEventStore();
	
	EventBus getEventBus();
	
}
