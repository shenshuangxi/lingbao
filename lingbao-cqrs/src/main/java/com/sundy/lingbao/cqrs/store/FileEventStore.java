package com.sundy.lingbao.cqrs.store;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.message.EventStream;
import com.sundy.lingbao.cqrs.message.SimpleEventStream;

public class FileEventStore implements EventStore {

	private final static Logger logger = LoggerFactory.getLogger(FileEventStore.class);
	
	private EventFile eventFile = new EventFile("/event");
	
	@Override
	public EventMessage<?> appendEvent(EventMessage<?> eventMessage) {
		return eventFile.appendEvent(eventMessage);
	}
	
	@Override
	public void appendSuccessEvent(EventMessage<?> eventMessage) {
		eventFile.appendSuccessEvent(eventMessage);;
	}

	@Override
	public void appendFailEvent(EventMessage<?> eventMessage) {
		eventFile.appendFailEvent(eventMessage);
	}

	@Override
	public EventStream readEvents(String aggrgateType, String aggregateIdentifier, Long version) {
		try {
			List<EventMessage<?>> eventMessages = eventFile.readFile(aggrgateType, aggregateIdentifier, version);
			SimpleEventStream eventStream = new SimpleEventStream(eventMessages);
			return eventStream;
		} catch (Throwable e) {
			logger.error("事件读取失败", e);
			throw new LingbaoException(-1, String.format("read [%s] aggreaget event [%d] version fail", aggregateIdentifier, version));
		}
	}

	@Override
	public EventStream readEvents(String aggrgateType, String aggregateIdentifier) {
		try {
			List<EventMessage<?>> eventMessages = eventFile.readFile(aggrgateType, aggregateIdentifier, null);
			SimpleEventStream eventStream = new SimpleEventStream(eventMessages);
			return eventStream;
		} catch (Throwable e) {
			logger.error("事件读取失败", e);
			throw new LingbaoException(-1, String.format("read [%s] aggreaget event fail", aggregateIdentifier));
		}
	}

}
