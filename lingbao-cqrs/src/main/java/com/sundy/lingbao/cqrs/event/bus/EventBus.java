package com.sundy.lingbao.cqrs.event.bus;

import com.sundy.lingbao.cqrs.event.listeners.EventListener;
import com.sundy.lingbao.cqrs.message.EventMessage;

public interface EventBus {

	<C, R> void publish(EventMessage<C> eventMessage);
	
	void registerListener(EventListener<EventMessage<?>> eventListener);
	
	void removeListener(EventListener<EventMessage<?>> eventListener);
	
}
