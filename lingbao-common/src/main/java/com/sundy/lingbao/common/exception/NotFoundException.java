package com.sundy.lingbao.common.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class NotFoundException extends AbstractLingbaoHttpException {

	public NotFoundException(String str) {
		super(str);
		setHttpStatus(HttpStatus.NOT_FOUND);
	}
}
