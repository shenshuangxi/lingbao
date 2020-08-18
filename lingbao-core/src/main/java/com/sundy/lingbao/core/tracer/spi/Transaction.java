package com.sundy.lingbao.core.tracer.spi;

public interface Transaction {

	final String SUCCESS = "0";
	
	public void setStatus(String status);
	
	public void setStatus(Throwable e);
	
	public void addData(String key, Object value);
	
	public void complete();
	
}
