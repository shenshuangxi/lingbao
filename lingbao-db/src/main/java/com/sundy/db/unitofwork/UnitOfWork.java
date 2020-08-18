package com.sundy.db.unitofwork;

import java.util.concurrent.Callable;

import com.sundy.db.action.DbAction;
import com.sundy.db.constant.DbConstant.DbTransactionIsolationLevel;

public interface UnitOfWork {

	void addDbAction(DbAction dbAction);
	
	void setTransactionIsolationLevel(DbTransactionIsolationLevel transactionIsolationLevel);
	
	<R> R execute(Callable<R> callable) throws Throwable;

}
