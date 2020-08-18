package com.sundy.lingbao.file.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Request {

	/**
	 * 请求方法
	 */
	private String rquestMethod;
	
	/**
	 * 请求路径
	 */
	private String requestPath;

	public Request(String rquestMethod, String requestPath) {
		this.rquestMethod = rquestMethod;
		this.requestPath = requestPath;
	}
	
	public String getRquestMethod() {
		return rquestMethod;
	}


	public String getRequestPath() {
		return requestPath;
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	
	
	
	
}
