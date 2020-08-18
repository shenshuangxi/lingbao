package com.sundy.lingbao.core.tracer.spi;

public interface MessageProducer {

	public void logError(Throwable cause);
	
	public void logError(String message, Throwable cause);
	
	public void logEvent(String type, String name);
	
	public void logEvent(String type, String name, String status, String nameValuePairs);
	
	public Transaction newTransaction(String type, String name);
	
}
