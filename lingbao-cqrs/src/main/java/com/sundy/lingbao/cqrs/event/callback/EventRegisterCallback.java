package com.sundy.lingbao.cqrs.event.callback;

import com.sundy.lingbao.cqrs.message.EventMessage;

public interface EventRegisterCallback {

	EventMessage<?> registerEvent(EventMessage<?> eventMessage);
	
}
