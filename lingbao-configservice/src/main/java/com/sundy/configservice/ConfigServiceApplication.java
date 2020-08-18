package com.sundy.configservice;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sundy.lingbao.biz.LingbaoBizConfig;
import com.sundy.metaservice.LingbaoMetaServiceConfig;


@EnableAspectJAutoProxy
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableEurekaServer
@Configuration
@PropertySource(value = {"classpath:configservice.properties"})
@ComponentScan(basePackageClasses={
		ConfigServiceApplication.class,
		LingbaoBizConfig.class,
		LingbaoMetaServiceConfig.class
})
public class ConfigServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(ConfigServiceApplication.class).run(args);
		context.addApplicationListener(new ApplicationPidFileWriter());
	}
	
	
	
}
