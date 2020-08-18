package com.sundy.lingbao.cqrs.aggregate;

import java.util.ArrayList;
import java.util.List;

import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.message.EventStream;
import com.sundy.lingbao.cqrs.message.GenericEventMessage;
import com.sundy.lingbao.cqrs.message.SimpleEventStream;

public class EventConatainer {

	private final String aggregateIdentifier;
	
	private final String aggregateType;
	
	private List<EventMessage<?>> events = new ArrayList<EventMessage<?>>();
	
	private Long lastCommitSequence;
	
	private Long lastSequenceNumber;

	public EventConatainer(String aggregateIdentifier, String aggregateType) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.aggregateType = aggregateType;
	}
	
	
	public String getAggregateIdentifier() {
		return this.aggregateIdentifier;
	}
	
	public void registerEvent(Object payload) {
		events.add(new GenericEventMessage<Object>(payload, aggregateIdentifier, newSequenceNumber(), aggregateType));
	}
	
	private Long newSequenceNumber() {
		Long currentSequenceNumber = getLastSequenceNumber();
		if (currentSequenceNumber==null) {
			return 0l;
		} 
		return currentSequenceNumber + 1;
	}
	
	public Long getLastSequenceNumber() {
		if (events.isEmpty()) {
			return lastCommitSequence;
		} else if (lastSequenceNumber==null) {
			lastSequenceNumber = events.get(events.size()-1).getSequenceNumber();
		}
		return lastSequenceNumber;
	}
	
	
	public EventStream getEventStream() {
		return new SimpleEventStream(events);
	}
	
	
	
	
	
}
