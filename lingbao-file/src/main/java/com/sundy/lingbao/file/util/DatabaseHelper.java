package com.sundy.lingbao.file.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DatabaseHelper {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
	
	private static ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();
	
	private static DataSource dataSource;
	
	static{
		Properties props = PropsUtil.loadProps("config.properties");
		HikariConfig config = new HikariConfig(props);
		dataSource = new HikariDataSource(config);
	}
	
	public static Connection getConnection(){
		Connection conn = CONNECTION_HOLDER.get();
		if(conn==null){
			try {
				conn = dataSource.getConnection();
				CONNECTION_HOLDER.set(conn);
			} catch (SQLException e) {
				logger.error("get connection failure", e);
			}
		}
		
		return conn;
	}
	
	public static void closeConnection(){
		Connection conn = CONNECTION_HOLDER.get();
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("close connection failure", e);
			}finally{
				CONNECTION_HOLDER.remove();
			}
		}
	}
	
}
