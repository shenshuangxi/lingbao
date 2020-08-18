package com.sundy.lingbao.common.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class ServiceException extends AbstractLingbaoHttpException {

	public ServiceException(String str) {
		super(str);
		setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ServiceException(String str, Exception e) {
		super(str, e);
		setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
