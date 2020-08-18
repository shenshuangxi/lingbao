package com.sundy.lingbao.cqrs.event.bus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.sundy.lingbao.cqrs.event.listeners.EventListener;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.store.EventStore;

public class MasterEventBus implements EventBus {

	private List<EventListener<EventMessage<?>>> listeners = new ArrayList<EventListener<EventMessage<?>>>();
	
	private final int DEFAULT_EVENT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
	
	private ReactorEventThread[] nioThreads;
	
	private ExecutorService businessExecutorPool;
	
	private final AtomicInteger count = new AtomicInteger(0);
	
	public MasterEventBus(Integer eventThreads, EventStore eventStore) {
		if (eventThreads==null || eventThreads<1) {
			eventThreads = DEFAULT_EVENT_THREAD_COUNT;
		}
		businessExecutorPool = Executors.newFixedThreadPool(eventThreads*4);
		nioThreads = new ReactorEventThread[eventThreads];
		for(int i=0;i<eventThreads;i++){
			nioThreads[i] = new ReactorEventThread(businessExecutorPool, listeners, eventStore);
			nioThreads[i].start();
		}
	}
	
	
	@Override
	public <C, R> void publish(EventMessage<C> eventMessage) {
		nioThreads[count.getAndIncrement()%nioThreads.length].register(eventMessage);
	}

	@Override
	public void registerListener(EventListener<EventMessage<?>> eventListener) {
		listeners.add(eventListener);
	}

	@Override
	public void removeListener(EventListener<EventMessage<?>> eventListener) {
		listeners.remove(eventListener);
	}

}
