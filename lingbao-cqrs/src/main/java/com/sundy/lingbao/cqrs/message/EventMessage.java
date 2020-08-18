package com.sundy.lingbao.cqrs.message;

import java.time.Instant;

public interface EventMessage<T> extends Message<T> {

	String getAggregateType();
	
	String getAggregateIdentifier();
	
	Long getSequenceNumber();
	
	
	void setAggregateType(String aggregateType);
	
	void setAggregateIdentifier(String aggregateIdentifier);
	
	void setSequenceNumber(Long sequenceNumber);
	
	Instant getTimestamp();
	
}
