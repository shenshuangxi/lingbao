package com.sundy.lingbao.core.exception;

public class LingbaoException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;
	private final String message;
	private final Integer code;
	
	public LingbaoException(Integer code, String message) {
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}
	
}
