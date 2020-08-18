package com.sundy.lingbao.cqrs.message.handler;

import com.sundy.lingbao.cqrs.message.Message;

public interface MessageHandler<M extends Message<?>> {

	Object handle(M message);
	
	default boolean match(M message) {
		return true;
	}
	
	

}
