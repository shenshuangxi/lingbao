package com.sundy.lingbao.cqrs.message.interceptor;

public interface InterceptorChain {

	public Object proceed() throws Exception;
	
}
