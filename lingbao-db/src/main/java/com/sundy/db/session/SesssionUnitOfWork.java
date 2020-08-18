package com.sundy.db.session;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class SesssionUnitOfWork {

	private SqlSessionFactory sqlSessionFactory;
	private ThreadLocal<SqlSession> session = new ThreadLocal<SqlSession>();
	
	public SqlSession getSession() {
		if (session.get() == null) {
			SqlSession sqlSession = sqlSessionFactory.openSession();
			session.set(sqlSession);
			return sqlSession;
		} else {
			return session.get();
		}
	}
	
	
}
