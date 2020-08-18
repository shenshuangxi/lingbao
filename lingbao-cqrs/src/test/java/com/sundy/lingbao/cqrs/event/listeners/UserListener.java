package com.sundy.lingbao.cqrs.event.listeners;

import com.sundy.lingbao.cqrs.aggregate.annotation.EventHandler;
import com.sundy.lingbao.cqrs.event.UserCreated;

public class UserListener {

	@EventHandler
	public void on(UserCreated userCreated) {
		System.out.println(userCreated.getName());
		System.out.println(userCreated.getAge());
	}
	
}
