package com.sundy.lingbao.cqrs.store;

import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.message.EventStream;

public interface EventStore {

	EventMessage<?> appendEvent(EventMessage<?> eventMessage);
	
	void appendSuccessEvent(EventMessage<?> eventMessage);
	
	void appendFailEvent(EventMessage<?> eventMessage);
	
	EventStream readEvents(String aggrgateType, String aggregateIdentifier, Long version);
	
	EventStream readEvents(String aggrgateType, String aggregateIdentifier);
	
}
