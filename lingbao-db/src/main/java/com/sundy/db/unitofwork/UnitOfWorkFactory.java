package com.sundy.db.unitofwork;

import com.sundy.db.DbContext;
import com.sundy.db.constant.DbConstant.UnitOfWorkType;

public interface UnitOfWorkFactory {

	UnitOfWork createUnitOfWork(UnitOfWorkType unitOfWorkType, DbContext dbContext);

}
