package com.sundy.lingbao.cqrs.message;

import java.time.Instant;

import com.sundy.lingbao.core.idgenerate.IdGenerateFactory;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Setter
@Getter
public class GenericEventMessage<T> implements EventMessage<T> {

	private String identifier;
	
	private T payload;
	
	private String payloadType;
	
	private String aggregateIdentifier;
	
	private String aggregateType;
	
	private Long sequenceNumber;
	
	private Instant timestamp;
	
	
	
	public GenericEventMessage() {
		
	}

	public GenericEventMessage(T payload, String aggregateIdentifier, Long sequenceNumber, String aggregateType) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.payload = payload;
		this.payloadType = payload.getClass().getName();
		this.sequenceNumber = sequenceNumber;
		this.aggregateType = aggregateType;
		this.identifier = IdGenerateFactory.getIdGenerator().getId();
		this.timestamp = Instant.now();
	}
	
	public GenericEventMessage(T payload) {
		this.payload = payload;
		this.payloadType = payload.getClass().getName();
		this.identifier = IdGenerateFactory.getIdGenerator().getId();
		this.timestamp = Instant.now();
	}

}
