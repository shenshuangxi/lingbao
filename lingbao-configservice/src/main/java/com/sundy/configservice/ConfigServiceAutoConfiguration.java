package com.sundy.configservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sundy.configservice.controller.NotificationController;
import com.sundy.lingbao.biz.message.DbReleaeMessageScanner;

@Configuration
public class ConfigServiceAutoConfiguration {

		
	@Autowired
	private NotificationController notificationController;
	
	@Bean
	public DbReleaeMessageScanner dbReleaeMessageScanner() {
		DbReleaeMessageScanner dbReleaeMessageScanner = new DbReleaeMessageScanner();
		dbReleaeMessageScanner.addMessageListener(notificationController);
		return dbReleaeMessageScanner;
	}
		
	
}
