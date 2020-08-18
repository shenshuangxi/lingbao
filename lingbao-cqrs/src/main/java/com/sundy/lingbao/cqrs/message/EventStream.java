package com.sundy.lingbao.cqrs.message;

public interface EventStream {

	boolean hasNext();
	
	EventMessage<?> next();
	
	EventMessage<?> peek();
	
}
