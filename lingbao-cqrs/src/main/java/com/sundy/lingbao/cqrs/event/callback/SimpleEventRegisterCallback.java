package com.sundy.lingbao.cqrs.event.callback;

import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.store.EventStore;

public class SimpleEventRegisterCallback implements EventRegisterCallback {

	private final EventStore eventStore;
	
	public SimpleEventRegisterCallback(EventStore eventStore) {
		this.eventStore = eventStore;
	}


	@Override
	public EventMessage<?> registerEvent(EventMessage<?> eventMessage) {
		return this.eventStore.appendEvent(eventMessage);
	}

}
