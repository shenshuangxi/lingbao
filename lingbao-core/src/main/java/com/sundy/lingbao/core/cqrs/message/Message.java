package com.sundy.lingbao.core.cqrs.message;

import java.io.Serializable;

public interface Message<T> extends Serializable {

	String getAggregateIdentifier();
	
	T getPayload();
	
	Class<?> getPayloadType();
	
}
