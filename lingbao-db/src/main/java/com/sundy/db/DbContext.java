package com.sundy.db;

import javax.sql.DataSource;

import com.sundy.db.session.DbSqlSessionFactory;
import com.sundy.db.session.Session;
import com.sundy.db.session.SessionFactory;
import com.sundy.db.store.DbEventStore;
import com.sundy.lingbao.core.idgenerate.IdGenerator;


public class DbContext {

	private SessionFactory querySqlSessionFactory;
	
	private IdGenerator idGenerator;
	
	private DbEventStore dbEventStore;
	
	public DbContext(DataSource queryDataSource, DataSource eventDataSource, IdGenerator idGenerator, String eventDir, Integer fileEventCount, Integer eventThreads) throws Exception{
		querySqlSessionFactory = new DbSqlSessionFactory(queryDataSource);
		dbEventStore = new DbEventStore(eventDir, fileEventCount, eventThreads, new DbSqlSessionFactory(eventDataSource));
		this.idGenerator = idGenerator;
	}
	
	public Session getSession(){
		return querySqlSessionFactory.openSession();
	}
	
	public String getId(){
		return idGenerator.getId();
	}
	
	public DbEventStore getDbEventStore() {
		return dbEventStore;
	}
	
}
