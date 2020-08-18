package com.sundy.lingbao.core.cqrs.message.event;

import java.time.Instant;

public class GenericEventMessage<T> implements EventMessage<T> {

	private static final long serialVersionUID = -1317545366487893746L;
	
	private final String aggregateIdentifier;
	
	private final T payload;
	
	private final Class<?> payloadType;
	
	private final String eventName;
	
	private final Long sequenceNumber;
	
	private final String aggregateType;
	
	private final Instant timeStamp;
	
	public GenericEventMessage(String aggregateIdentifier, String aggregateType, Long sequencNumber, T payload) {
		this.payload = payload;
		this.payloadType = payload.getClass();
		this.eventName = payload.getClass().getName();
		this.aggregateIdentifier = aggregateIdentifier;
		this.aggregateType = aggregateType;
		this.sequenceNumber = sequencNumber;
		this.timeStamp = Instant.now();
	}
	
	@Override
	public String getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	@Override
	public T getPayload() {
		return payload;
	}

	@Override
	public Class<?> getPayloadType() {
		return payloadType;
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	@Override
	public String getAggregateType() {
		return aggregateType;
	}

	@Override
	public Instant getTimeStamp() {
		return timeStamp;
	}

}
