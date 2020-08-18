package com.sundy.db.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sundy.lingbao.core.exception.LingbaoException;

public class SimpleEventBus implements EventBus {

	private final ConcurrentMap<String, List<EventListener>> subscriptions = new ConcurrentHashMap<String, List<EventListener>>();
	
	private final int DEFAULT_EVENT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();

	private ExecutorService businessExecutorPool = Executors.newFixedThreadPool(DEFAULT_EVENT_THREAD_COUNT*4);
	
	@Override
	public <E> void publish(E event) {
		businessExecutorPool.submit(new Runnable() {
			@Override
			public void run() {
				final List<EventListener> eventListeners = subscriptions.get(event.getClass().getName());
				if (eventListeners!=null && eventListeners.size()>0) {
					for (EventListener eventListener : eventListeners) {
						eventListener.handle(event);
					}
				}
			}
		});
	}

	@Override
	public void registerListener(String eventName, EventListener eventListener) {
		if (!subscriptions.containsKey(eventName)) {
			subscriptions.put(eventName, new CopyOnWriteArrayList<EventListener>());
		}
		if (subscriptions.get(eventName).contains(eventListener)) {
			throw new LingbaoException(-1, String.format("[%s] event  relation [%s] eventlistener is exists", eventName, eventListener.getClass().getSimpleName()));
		} 
		subscriptions.get(eventName).add(eventListener);
	}

	@Override
	public void removeListener(String eventName, EventListener eventListener) {
		if (subscriptions.containsKey(eventName) && subscriptions.get(eventName).contains(eventListener)) {
			subscriptions.get(eventName).remove(eventListener);
		}
	}



}
