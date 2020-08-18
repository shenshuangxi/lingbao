package com.sundy.db.unitofwork;

import com.sundy.db.DbContext;
import com.sundy.db.constant.DbConstant.UnitOfWorkType;

public class DefaultUnitOfWorkFactory implements UnitOfWorkFactory {

	@Override
	public UnitOfWork createUnitOfWork(UnitOfWorkType unitOfWorkType, DbContext dbContext) {
		return DefaultUnitOfWork.create(unitOfWorkType, dbContext);
	}

}
