package com.sundy.db.event;

public interface EventListener {

	<E> void handle(E event);
	
}
