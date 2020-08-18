package com.sundy.db.store.event;

import java.util.ArrayList;
import java.util.List;

import com.sundy.db.action.DbAction;
import com.sundy.db.constant.DbConstant.DbTransactionIsolationLevel;
import com.sundy.db.session.Session;
import com.sundy.db.store.event.DbEvent;

@SuppressWarnings("serial")
public class DefalutDbEvent implements DbEvent {

	private List<DbAction> dbActions = new ArrayList<DbAction>();
	private DbTransactionIsolationLevel dbTransactionIsolationLevel;
	
	public DefalutDbEvent(){
	}
	
	public DefalutDbEvent(List<DbAction> dbActions, DbTransactionIsolationLevel dbTransactionIsolationLevel){
		this.dbActions = dbActions;
		this.dbTransactionIsolationLevel = dbTransactionIsolationLevel;
	}
	
	public void addDbAction(DbAction dbAction) {
		dbActions.add(dbAction);
	}
	
	public List<DbAction> getDbActions() {
		return dbActions;
	}
	
	public DbTransactionIsolationLevel getDbTransactionIsolationLevel() {
		return dbTransactionIsolationLevel;
	}

	public void setDbTransactionIsolationLevel(DbTransactionIsolationLevel dbTransactionIsolationLevel) {
		this.dbTransactionIsolationLevel = dbTransactionIsolationLevel;
	}

	@Override
	public void execute(Session session) {
		session.execute(dbActions,dbTransactionIsolationLevel);
	}

}
