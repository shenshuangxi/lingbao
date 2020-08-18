package com.sundy.lingbao.core.cqrs.exception;

@SuppressWarnings("serial")
public class LingbaoCqrsException extends RuntimeException{

	public LingbaoCqrsException() {
        super();
    }
	
	public LingbaoCqrsException(String message) {
        super(message);
    }
	
	public LingbaoCqrsException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public LingbaoCqrsException(Throwable cause) {
        super(cause);
    }
	
}
