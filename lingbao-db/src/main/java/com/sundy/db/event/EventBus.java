package com.sundy.db.event;

public interface EventBus {

	<E> void publish(E event);
	
	void registerListener(String eventName, EventListener eventListener);
	
	void removeListener(String eventName, EventListener eventListener);
	
}
