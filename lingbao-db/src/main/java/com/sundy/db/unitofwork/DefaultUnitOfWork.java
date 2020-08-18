package com.sundy.db.unitofwork;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.sundy.db.DbContext;
import com.sundy.db.action.DbAction;
import com.sundy.db.action.DbActionInsert;
import com.sundy.db.constant.DbConstant.DbTransactionIsolationLevel;
import com.sundy.db.constant.DbConstant.UnitOfWorkType;
import com.sundy.lingbao.core.util.StringUtils;

public class DefaultUnitOfWork implements UnitOfWork {

	private ArrayList<DbAction> dbActions = new ArrayList<DbAction>();
	private DbTransactionIsolationLevel transactionIsolationLevel = DbTransactionIsolationLevel.READ_COMMITTED; 
	private UnitOfWorkType unitOfWorkType;
	private DbContext dbContext;
	
	private DefaultUnitOfWork(UnitOfWorkType unitOfWorkType, DbContext dbContext){
		this.unitOfWorkType = unitOfWorkType;
		this.dbContext = dbContext;
	}
	
	@Override
	public <R> R execute(Callable<R> callable) throws Throwable {
		R ret;
		try {
			ret = callable.call();
			if(unitOfWorkType.getCode()==UnitOfWorkType.SESSION.getCode()){
				dbContext.getSession().execute(dbActions, transactionIsolationLevel);
			} else {
				dbContext.getDbEventStore().dispatchDbEvent(dbActions, transactionIsolationLevel);
			}
			return ret;
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally{
			CurrentUnitOfWork.clear(this);
		}
	}

	public static UnitOfWork create(UnitOfWorkType unitOfWorkType, DbContext dbContext) {
		DefaultUnitOfWork uow = new DefaultUnitOfWork(unitOfWorkType, dbContext);
		CurrentUnitOfWork.set(uow);
		return uow;
	}

	@Override
	public void addDbAction(DbAction dbAction) {
		if (dbAction instanceof DbActionInsert) {
			if (StringUtils.isNullOrEmpty(((DbActionInsert) dbAction).getPersistEntity().getId())) {
				((DbActionInsert) dbAction).getPersistEntity().setId(dbContext.getId());
			}
		}
		dbActions.add(dbAction);
	}

	public void setTransactionIsolationLevel(DbTransactionIsolationLevel transactionIsolationLevel) {
		this.transactionIsolationLevel = transactionIsolationLevel;
	}

	

}
