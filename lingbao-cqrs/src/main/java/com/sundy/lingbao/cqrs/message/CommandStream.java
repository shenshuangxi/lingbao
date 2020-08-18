package com.sundy.lingbao.cqrs.message;

public interface CommandStream {

	boolean hasNext();
	
	CommandMessage<?> next();
	
	CommandMessage<?> peek();
	
}
