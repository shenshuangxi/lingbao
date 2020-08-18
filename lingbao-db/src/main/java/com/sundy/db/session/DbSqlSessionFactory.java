package com.sundy.db.session;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.sundy.db.entity.PersistEntity;
import com.sundy.db.util.FileUtil;

public class DbSqlSessionFactory implements SessionFactory {

	private SqlSessionFactory sqlSessionFactory;
	private Map<Class<?>, String> insertStatements = new HashMap<Class<?>, String>();
	private Map<Class<?>, String> updateStatements = new HashMap<Class<?>, String>();
	private Map<Class<?>, String> deleteStatements = new HashMap<Class<?>, String>();
	private Map<Class<?>, String> selectStatements = new HashMap<Class<?>, String>();
	
	public DbSqlSessionFactory(DataSource dataSource) throws Exception {
		sqlSessionFactory = buildSqlSessionFactory(dataSource);
	}
	
	
	public SqlSessionFactory buildSqlSessionFactory(DataSource dataSource) throws Exception{
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.setCallSettersOnNulls(true);
		URL[] urls = FileUtil.getURLs("classpath*:META-INF/db/mapper/*.xml");
		if(urls!=null&&urls.length>0){
			for(URL url : urls){
				if(url==null) {
					continue;
				}
				try { 
					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(url.openStream(), configuration, url.getFile(), configuration.getSqlFragments());
					xmlMapperBuilder.parse();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					ErrorContext.instance().reset();
				}
			}
		}
		return new SqlSessionFactoryBuilder().build(configuration);
	}
	
	@Override
	public Session openSession() {
		return new DbSqlSession(this, sqlSessionFactory);
	}

	// insert, update and delete statements
	public String getInsertStatement(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, insertStatements, "insert", "");
	}

	public String getUpdateStatement(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, updateStatements, "update", "");
	}

	public String getDeleteStatement(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, deleteStatements, "delete", "");
	}

	public String getSelectStatement(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, selectStatements, "select", "");
	}
	
	public String getSelectStatementSelective(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, selectStatements, "select", "Selective");
	}
	
	public String getSelectStatementSelectiveCount(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, selectStatements, "select", "SelectiveCount");
	}
	
	public String selectSelectiveQo(Class<? extends PersistEntity> clazz) {
		return getStatement(clazz, selectStatements, "select", "SelectiveQo");
	}

	private String getStatement(Class<?> persistentObjectClass, Map<Class<?>, String> cachedStatements, String prefix, String suffix) {
		String statement = prefix + persistentObjectClass.getSimpleName()+suffix;
		return statement;
	}


	


	

}
