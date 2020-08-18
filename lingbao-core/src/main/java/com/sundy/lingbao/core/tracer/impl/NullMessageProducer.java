package com.sundy.lingbao.core.tracer.impl;

import com.sundy.lingbao.core.tracer.spi.MessageProducer;
import com.sundy.lingbao.core.tracer.spi.Transaction;

public class NullMessageProducer implements MessageProducer {

	private static final Transaction NULL_TRANSACTION = new NullTransaction();
	
	@Override
	public void logError(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logError(String message, Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logEvent(String type, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logEvent(String type, String name, String status,
			String nameValuePairs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Transaction newTransaction(String type, String name) {
		return NULL_TRANSACTION;
	}

}
