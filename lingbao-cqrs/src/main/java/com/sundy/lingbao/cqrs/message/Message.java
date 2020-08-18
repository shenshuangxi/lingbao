package com.sundy.lingbao.cqrs.message;

import java.io.Serializable;

public interface Message<T> extends Serializable {

	String getIdentifier();
	
	T getPayload();
	
	String getPayloadType();
	
}
