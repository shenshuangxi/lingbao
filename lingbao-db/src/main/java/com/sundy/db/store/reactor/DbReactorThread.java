package com.sundy.db.store.reactor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.db.session.SessionFactory;
import com.sundy.db.store.event.WrapEvent;
import com.sundy.db.store.util.EventFile;

public class DbReactorThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(DbReactorThread.class);
	
	private final ExecutorService businessExecutorPool;
	private final SessionFactory sessionFactory;
	private final EventFile eventFile;
	private LinkedBlockingDeque<WrapEvent> events = new LinkedBlockingDeque<WrapEvent>();
	
	public DbReactorThread(ExecutorService businessExecutorPool, SessionFactory sessionFactory, EventFile eventFile) {
		this.businessExecutorPool = businessExecutorPool;
		this.sessionFactory = sessionFactory;
		this.eventFile = eventFile;
	}
	
	public void register(WrapEvent event) {
		events.add(event);
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()){
			WrapEvent wrapEvent = events.poll();
			if(wrapEvent!=null){
				dispatch(wrapEvent);
			} else {
				try {
					Thread.sleep(1000);
				} catch (Throwable e) {
					e.printStackTrace();
					logger.error("线程出错", e);
				}
			}
		}
	}

	private void dispatch(WrapEvent wrapEvent) {
		Future<?> future = businessExecutorPool.submit(new DbTask(wrapEvent,sessionFactory));
		try {
			future.get();
			this.eventFile.appendSuccessEvent(wrapEvent);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("执行出错", e);
			this.eventFile.appendFailEvent(wrapEvent);
		}
	}

}
