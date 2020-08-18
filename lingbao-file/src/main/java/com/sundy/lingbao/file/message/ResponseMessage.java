package com.sundy.lingbao.file.message;

public class ResponseMessage {

	private Boolean isSuccess;
	private String message;
	private Object body;
	
	
	public ResponseMessage() {
		this.isSuccess = true;
		this.message = "operator success";
	}
	
	public ResponseMessage(Boolean isSuccess, String message) {
		this.isSuccess = isSuccess;
		this.message = message;
	}
	
	
	
	public ResponseMessage(Boolean isSuccess, String message, Object body) {
		this.isSuccess = isSuccess;
		this.message = message;
		this.body = body;
	}



	public Boolean getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	
	
	
}
