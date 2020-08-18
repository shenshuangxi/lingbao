package com.sundy.lingbao.cqrs.command.gateway;

import java.util.concurrent.TimeUnit;

public interface CommandGateway {

	<C, R> R sendAndWait(C command);
	
	<C, R> R sendAndWait(C command, Long timeout, TimeUnit timeUnit);
	
	<C> void send(C command);
	
}
