package com.sundy.lingbao.core.cqrs.message.event;

import java.time.Instant;

import com.sundy.lingbao.core.cqrs.message.Message;

public interface EventMessage<T> extends Message<T> {

	String getEventName();
	
	Long getSequenceNumber();
	
	String getAggregateType();
	
	Instant getTimeStamp();
	
}
