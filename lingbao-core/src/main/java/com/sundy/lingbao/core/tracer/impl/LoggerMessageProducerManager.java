package com.sundy.lingbao.core.tracer.impl;

import com.sundy.lingbao.core.tracer.spi.MessageProducer;
import com.sundy.lingbao.core.tracer.spi.MessageProducerManager;

public class LoggerMessageProducerManager implements MessageProducerManager {

	private final static MessageProducer messageProducer = new LoggerMessageProducer();
	
	@Override
	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

}
