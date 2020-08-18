package com.sundy.lingbao.core.tracer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.sundy.lingbao.core.tracer.spi.MessageProducer;
import com.sundy.lingbao.core.tracer.spi.Transaction;

public class LoggerMessageProducer implements MessageProducer {

	private final Logger logger = LoggerFactory.getLogger(LoggerMessageProducer.class);
	
	
	@Override
	public void logError(Throwable cause) {
		logger.error("", cause);
	}

	@Override
	public void logError(String message, Throwable cause) {
		logger.error(message, cause);
	}

	@Override
	public void logEvent(String type, String name) {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(this).omitNullValues().add("type", type).add("name", name);
		logger.info(toStringHelper.toString());
	}

	@Override
	public void logEvent(String type, String name, String status,
			String nameValuePairs) {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(this).omitNullValues()
				.add("type", type)
				.add("name", name)
				.add("status", status)
				.add("nameValuePairs", nameValuePairs);
		logger.info(toStringHelper.toString());
	}

	@Override
	public Transaction newTransaction(String type, String name) {
		return new LoggerTransaction();
	}
	

}
