package com.sundy.db.command;

public interface CommandHandler<C> {

	Object handle(C command) throws Exception;
	
}
