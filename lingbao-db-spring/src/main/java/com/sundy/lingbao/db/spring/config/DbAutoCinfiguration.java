package com.sundy.lingbao.db.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.sundy.db.DbContext;
import com.sundy.db.command.CommandBus;
import com.sundy.db.command.SimpleCommandBus;
import com.sundy.db.event.EventBus;
import com.sundy.db.event.SimpleEventBus;
import com.sundy.lingbao.core.idgenerate.IdGenerator;
import com.sundy.lingbao.core.idgenerate.MachineIdGenerator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DbAutoCinfiguration {

	@Autowired
	private ConfigurableEnvironment environment;
	
	@Autowired
	private DbConfig dbConfig;
	
	@Bean
	public CommandBus commandBus() throws Exception {
		SimpleCommandBus commandBus = new SimpleCommandBus();
		commandBus.setDbContext(dbContext());
		return commandBus;
	}
	
	@Bean
	public EventBus eventBus() throws Exception {
		EventBus eventBus = new SimpleEventBus();
		return eventBus;
	}
	
	@Bean
	public DbContext dbContext() throws Exception {
		DbContext dbContext = new DbContext(queryDataSource(), eventDataSource(), idGenerator(dbConfig.getWorkerId(), dbConfig.getDatacenterId()), dbConfig.getEventDir(), dbConfig.getFileEventCount(), dbConfig.getEventThreads());
		return dbContext;
	}
	
	@Bean
	public DataSource queryDataSource() {
		HikariConfig config = new HikariConfig(dbConfig.getQueryDataSourcePropertyPath());
		HikariDataSource hikariDataSource = new HikariDataSource(config);
		return hikariDataSource;
	}
	
	@Bean
	public DataSource eventDataSource() {
		HikariConfig config = new HikariConfig(dbConfig.getEventDataSourcePropertyPath());
		HikariDataSource hikariDataSource = new HikariDataSource(config);
		return hikariDataSource;
	}
	
	@Bean
	public IdGenerator idGenerator(String workerId, String datacenterId) {
		MachineIdGenerator machineIdGenerator = new MachineIdGenerator(workerId, datacenterId);
		return machineIdGenerator;
	}
	
	
}
