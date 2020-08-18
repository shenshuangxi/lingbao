package com.sundy.db.session;

import java.util.List;

import com.sundy.db.action.DbAction;
import com.sundy.db.constant.DbConstant.DbTransactionIsolationLevel;
import com.sundy.db.entity.PageRequest;
import com.sundy.db.entity.PersistEntity;

public interface Session {

	public <T extends PersistEntity> List<T> selectSelective(T t);
	
	public <T extends PersistEntity> Long selectSelectiveCount(T t);
	
	public <T extends PersistEntity> List<T> selectSelectiveQo(T t, PageRequest pageRequest);
	
	public <T extends PersistEntity> T selectById(Class<T> clazz,Object id);
	
	public Object selectOne(String statement,Object parameter);
	
	public List<?> selectList(String statement,Object parameter);
	
	public void insert(PersistEntity persistObject);
	
	public void update(PersistEntity persistObject);
	
	public void delete(PersistEntity persistObject);
	
	public void execute(List<DbAction> dbActions, DbTransactionIsolationLevel transactionIsolationLevel);

}
