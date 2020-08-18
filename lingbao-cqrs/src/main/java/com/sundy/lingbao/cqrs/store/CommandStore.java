package com.sundy.lingbao.cqrs.store;

import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.message.CommandStream;

public interface CommandStore {

	CommandMessage<?> appendCommand(CommandMessage<?> commandMessage);
	
	void appendSuccessCommand(CommandMessage<?> commandMessage);
	
	void appendFailCommand(CommandMessage<?> commandMessage);
	
	CommandStream readUndoCommands();
	
}
