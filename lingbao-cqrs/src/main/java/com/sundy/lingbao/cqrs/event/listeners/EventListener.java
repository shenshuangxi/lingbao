package com.sundy.lingbao.cqrs.event.listeners;

import com.sundy.lingbao.cqrs.message.EventMessage;

public interface EventListener<E extends EventMessage<?>> {

	void handle(E eventMessage);
	
}
