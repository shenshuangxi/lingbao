package com.sundy.lingbao.core.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LingbaoThreadFactory implements ThreadFactory {

	private static Logger logger = LoggerFactory.getLogger(LingbaoThreadFactory.class);

	private final AtomicLong threadNumber = new AtomicLong(1);

	private final String namePrefix;

	private final boolean daemon;

	private static final ThreadGroup threadGroup = new ThreadGroup("lingbao");

	public static ThreadGroup getThreadGroup() {
	    return threadGroup;
	}

	private LingbaoThreadFactory(String namePrefix, boolean daemon) {
	    this.namePrefix = namePrefix;
	    this.daemon = daemon;
	}
	
	public static ThreadFactory create(String namePrefix, boolean daemon) {
	    return new LingbaoThreadFactory(namePrefix, daemon);
	}
	
	public static boolean waitAllShutdown(int timeoutInMillis) {
	    ThreadGroup group = getThreadGroup();
	    Thread[] activeThreads = new Thread[group.activeCount()];
	    group.enumerate(activeThreads);
	    Set<Thread> alives = new HashSet<Thread>(Arrays.asList(activeThreads));
	    Set<Thread> dies = new HashSet<Thread>();
	    logger.info("Current ACTIVE thread count is: {}", alives.size());
	    long expire = System.currentTimeMillis() + timeoutInMillis;
	    while (System.currentTimeMillis() < expire) {
	      classify(alives, dies);
	      if (alives.size() > 0) {
	    	  logger.info("Alive lingbao threads: {}", alives);
	        try {
	          TimeUnit.SECONDS.sleep(2);
	        } catch (InterruptedException ex) {
	          // ignore
	        }
	      } else {
	    	  logger.info("All lingbao threads are shutdown.");
	    	  return true;
	      }
	    }
	    logger.warn("Some lingbao threads are still alive but expire time has reached, alive threads: {}", alives);
	    return false;
	}
	
	
	private static  void classify(Set<Thread> src, Set<Thread> des) {
		Set<Thread> set = new HashSet<>();
	    for (Thread thread : src) {
	      if (!thread.isAlive() || thread.isInterrupted() || thread.isDaemon()) {
	    	  set.add(thread);
	      }
	    }
	    src.removeAll(set);
	    des.addAll(set);
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(threadGroup, runnable, threadGroup.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
	    thread.setDaemon(daemon);
	    if (thread.getPriority() != Thread.NORM_PRIORITY) {
	      thread.setPriority(Thread.NORM_PRIORITY);
	    }
	    return thread;
	}

}
