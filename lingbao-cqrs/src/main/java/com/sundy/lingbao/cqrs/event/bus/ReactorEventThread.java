package com.sundy.lingbao.cqrs.event.bus;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.cqrs.event.listeners.EventListener;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.store.EventStore;

public class ReactorEventThread extends Thread  {

	private static Logger logger = LoggerFactory.getLogger(ReactorEventThread.class);
	
	private final List<EventListener<EventMessage<?>>> listeners;
	
	private final EventStore eventStore;
	
	private final ExecutorService businessExecutorPool;
	
	private LinkedBlockingDeque<EventMessage<?>> tasks = new LinkedBlockingDeque<EventMessage<?>>();
	
	public void register(EventMessage<?> task) {
		tasks.offer(task);
	}
	
	public ReactorEventThread(ExecutorService businessExecutorPool, List<EventListener<EventMessage<?>>> listeners, EventStore eventStore) {
		this.businessExecutorPool = businessExecutorPool;
		this.listeners = listeners;
		this.eventStore = eventStore;
	}

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				EventMessage<?> eventMessage = tasks.take();
					Future<?> future = this.businessExecutorPool.submit(new Runnable() {
						@Override
						public void run() {
							try {
								for (EventListener<EventMessage<?>> eventListener : listeners) {
									eventListener.handle(eventMessage);
								}
							} catch (Throwable e) {
								logger.error(e.getMessage(), e);
								ReactorEventThread.this.eventStore.appendFailEvent(eventMessage);
							}
						}
					});
					try {
						Object obj = future.get();
						System.out.println(obj);
						this.eventStore.appendSuccessEvent(eventMessage);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			
		}
	}
	
	
	
}
