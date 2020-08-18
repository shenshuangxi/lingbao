package com.sundy.db.session;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

import com.sundy.db.action.DbAction;
import com.sundy.db.constant.DbConstant.DbTransactionIsolationLevel;
import com.sundy.db.entity.PageRequest;
import com.sundy.db.entity.PersistEntity;
import com.sundy.db.util.ClassUtil;

public class DbSqlSession implements Session {

	private SqlSessionFactory sqlSessionFactory;
	private DbSqlSessionFactory dbSqlSessionFactory;
	private SqlSession sqlSession;
	
	public DbSqlSession(DbSqlSessionFactory dbSqlSessionFactory, SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.dbSqlSessionFactory = dbSqlSessionFactory;
	}
	
	private TransactionIsolationLevel getTransactionIsolationLevel(DbTransactionIsolationLevel dbTransactionIsolationLevel){
		switch (dbTransactionIsolationLevel) {
			case SERIALIZABLE:
				return TransactionIsolationLevel.SERIALIZABLE;
			case REPEATABLE_READ:
				return TransactionIsolationLevel.REPEATABLE_READ;
			case READ_COMMITTED:
				return TransactionIsolationLevel.READ_COMMITTED;
			case READ_UNCOMMITTED:
				return TransactionIsolationLevel.READ_UNCOMMITTED;
			default:
				return TransactionIsolationLevel.NONE;
		}
	}
	
	@Override
	public void execute(List<DbAction> dbActions, DbTransactionIsolationLevel dbTransactionIsolationLevel) {
		try {
			sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(dbTransactionIsolationLevel));
			for (DbAction dbAction : dbActions) {
				dbAction.execute(this);
			}
			sqlSession.commit();
		} catch (Throwable e) {
			sqlSession.rollback();
			throw e;
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public void insert(PersistEntity persistEntity){
		this.sqlSession.insert(dbSqlSessionFactory.getInsertStatement(persistEntity.getClass()), persistEntity);
	}
	
	@Override
	public void update(PersistEntity persistEntity){
		this.sqlSession.update(dbSqlSessionFactory.getUpdateStatement(persistEntity.getClass()), persistEntity);
	}
	
	@Override
	public void delete(PersistEntity persistEntity){
		this.sqlSession.delete(dbSqlSessionFactory.getDeleteStatement(persistEntity.getClass()), persistEntity);
	}
	
	@Override
	public <T extends PersistEntity> List<T> selectSelective(T t) {
		SqlSession sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(DbTransactionIsolationLevel.READ_COMMITTED));
		try {
			String statement = dbSqlSessionFactory.getSelectStatementSelective(t.getClass());
			List<T> ts = sqlSession.selectList(statement, t);
			return ts;
		} finally{
			sqlSession.close();
		}
	}
	
	public <T extends PersistEntity> T selectById(Class<T> clazz, Object id){
		SqlSession sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(DbTransactionIsolationLevel.READ_COMMITTED));
		try {
			String statement = dbSqlSessionFactory.getSelectStatement(clazz);
			T t = sqlSession.selectOne(statement, id);
			return t;
		} finally{
			sqlSession.close();
		}
	}
	
	public Object selectOne(String statement,Object parameter){
		SqlSession sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(DbTransactionIsolationLevel.READ_COMMITTED));
		try {
			Object value = sqlSession.selectOne(statement, parameter);
			return value;
		} finally{
			sqlSession.close();
		}
	}
	
	public List<?> selectList(String statement,Object parameter){
		SqlSession sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(DbTransactionIsolationLevel.READ_COMMITTED));
		try {
			List<?> values = sqlSession.selectList(statement, parameter);
			return values;
		} finally{
			sqlSession.close();
		}
	}

	@Override
	public <T extends PersistEntity> Long selectSelectiveCount(T t) {
		SqlSession sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(DbTransactionIsolationLevel.READ_COMMITTED));
		try {
			String statement = dbSqlSessionFactory.getSelectStatementSelectiveCount(t.getClass());
			Long count = sqlSession.selectOne(statement, t);
			return count;
		} finally{
			sqlSession.close();
		}
	}

	@Override
	public <T extends PersistEntity> List<T> selectSelectiveQo(T t, PageRequest pageRequest) {
		SqlSession sqlSession = sqlSessionFactory.openSession(getTransactionIsolationLevel(DbTransactionIsolationLevel.READ_COMMITTED));
		try {
			String statement = dbSqlSessionFactory.selectSelectiveQo(t.getClass());
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("pageRequest", pageRequest.getPageRequest());
			Map<String,Field> fieldMaps = ClassUtil.findAllFieldMap(t.getClass());
			for (Entry<String, Field> entry : fieldMaps.entrySet()) {
				try {
					Field field = entry.getValue();
					field.setAccessible(true);
					parameter.put(entry.getKey(), entry.getValue().get(t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			List<T> values = sqlSession.selectList(statement, parameter);
			return values;
		} finally{
			sqlSession.close();
		}
	}

	

	

}
