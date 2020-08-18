package com.sundy.lingbao.common.exception;

import org.springframework.http.HttpStatus;

public class AbstractLingbaoHttpException extends RuntimeException {

	private static final long serialVersionUID = -1713129594004951820L;
	  
	protected Integer statusCode;

	public AbstractLingbaoHttpException(String msg){
	    super(msg);
	}
	  
	public AbstractLingbaoHttpException(String msg, Exception e){
	    super(msg,e);
	}

	protected void setHttpStatus(HttpStatus httpStatus){
	    this.statusCode = httpStatus.value();
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}


}
