package com.sundy.db.store;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.db.action.DbAction;
import com.sundy.db.constant.DbConstant.DbTransactionIsolationLevel;
import com.sundy.db.session.SessionFactory;
import com.sundy.db.store.event.DbEvent;
import com.sundy.db.store.event.DefalutDbEvent;
import com.sundy.db.store.event.WrapEvent;
import com.sundy.db.store.reactor.DbReactorThread;
import com.sundy.db.store.util.Base64Util;
import com.sundy.db.store.util.EventFile;

public class DbEventStore {

	private static Logger logger = LoggerFactory.getLogger(DbEventStore.class);
	
	private final int DEFAULT_EVENT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
	
	private DbReactorThread[] nioThreads;
	
	private ExecutorService businessExecutorPool;
	
	private final EventFile eventFile;
	
	private final static AtomicInteger count = new AtomicInteger(0);
	
	public DbEventStore(String eventDir, Integer fileEventCount, Integer eventThreads, SessionFactory sessionFactory){
		if(eventThreads==null || eventThreads<1){
			eventThreads = DEFAULT_EVENT_THREAD_COUNT;
		}
		eventFile = new EventFile(eventDir,fileEventCount);
		businessExecutorPool = Executors.newFixedThreadPool(eventThreads*4);
		nioThreads = new DbReactorThread[eventThreads];
		for(int i=0;i<eventThreads;i++){
			nioThreads[i] = new DbReactorThread(businessExecutorPool,sessionFactory, eventFile);
			nioThreads[i].start();
		}
		List<DbEvent> dbEvents = eventFile.getUndoEvents();
		dbEvents.addAll(eventFile.getFailEvents());
		if(dbEvents!=null){
			try {
				for(DbEvent dbEvent : dbEvents) {
					WrapEvent wrapEvent = eventFile.appendEvent(dbEvent);
					nioThreads[count.getAndIncrement()%nioThreads.length].register(wrapEvent);
				}
				logger.info("初始化事件队列完成");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("初始化事件队列失败", e);
			}
		}
	}
	
	public void dispatchDbEvent(List<DbAction> dbActions, DbTransactionIsolationLevel transactionIsolationLevel){
		try {
			if(dbActions!=null && !dbActions.isEmpty()){
				WrapEvent wrapEvent = eventFile.appendEvent(new DefalutDbEvent(dbActions, transactionIsolationLevel));
				nioThreads[count.getAndIncrement()%nioThreads.length].register(wrapEvent);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				logger.error(Base64Util.encodeObject(new DefalutDbEvent(dbActions, transactionIsolationLevel)), e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void dispatchDbEvent(DbAction... dbActions){
		try {
			if(dbActions!=null && dbActions.length>0){
				WrapEvent wrapEvent = eventFile.appendEvent(new DefalutDbEvent(Arrays.asList(dbActions), DbTransactionIsolationLevel.READ_COMMITTED));
				nioThreads[count.getAndIncrement()%nioThreads.length].register(wrapEvent);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				logger.error(Base64Util.encodeObject(new DefalutDbEvent(Arrays.asList(dbActions), DbTransactionIsolationLevel.READ_COMMITTED)), e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	
}
