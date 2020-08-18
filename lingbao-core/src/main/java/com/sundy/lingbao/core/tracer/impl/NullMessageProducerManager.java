package com.sundy.lingbao.core.tracer.impl;

import com.sundy.lingbao.core.tracer.spi.MessageProducer;
import com.sundy.lingbao.core.tracer.spi.MessageProducerManager;

public class NullMessageProducerManager implements MessageProducerManager {

	private static final MessageProducer producer = new NullMessageProducer();

	@Override
	public MessageProducer getMessageProducer() {
	    return producer;
	}
	
}
