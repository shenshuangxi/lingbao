package com.sundy.lingbao.common.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class BadRequestException extends AbstractLingbaoHttpException {

	public BadRequestException(String str) {
	    super(str);
	    setHttpStatus(HttpStatus.BAD_REQUEST);
	}
	
}
