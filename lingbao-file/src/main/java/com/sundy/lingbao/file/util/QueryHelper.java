package com.sundy.lingbao.file.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHelper {

	private static final Logger logger = LoggerFactory.getLogger(QueryHelper.class);
	
	private static final QueryRunner QUERY_RUNNER = new QueryRunner();
	
	public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params){
		List<T> entityList = null;
		Connection conn = DatabaseHelper.getConnection();
		try {
			entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
		} catch (SQLException e) {
			logger.error("query entity failure", e);
			throw new RuntimeException(e);
		}finally{
			DatabaseHelper.closeConnection();
		}
		return entityList;
	}
	
}
