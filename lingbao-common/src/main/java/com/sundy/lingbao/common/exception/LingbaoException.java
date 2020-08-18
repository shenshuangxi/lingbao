package com.sundy.lingbao.common.exception;


public class LingbaoException extends AbstractLingbaoHttpException {
    
	private static final long serialVersionUID = 1L;
	private final String errorMsg;
	private final Integer statusCode;
	
	public LingbaoException(Integer statusCode, String errorMsg) {
		super(errorMsg);
		this.errorMsg = errorMsg;
		this.statusCode = statusCode;
		setStatusCode(statusCode);
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

}
