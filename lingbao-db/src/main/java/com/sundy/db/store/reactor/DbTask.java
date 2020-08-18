package com.sundy.db.store.reactor;

import com.sundy.db.session.SessionFactory;
import com.sundy.db.store.event.WrapEvent;

public class DbTask implements Runnable {

	private final WrapEvent wrapEvent ;
	private final SessionFactory sessionFactory;
	
	public DbTask(WrapEvent wrapEvent, SessionFactory sessionFactory) {
		this.wrapEvent = wrapEvent;
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void run() {
		wrapEvent.getDbEvent().execute(this.sessionFactory.openSession());
	}

}
