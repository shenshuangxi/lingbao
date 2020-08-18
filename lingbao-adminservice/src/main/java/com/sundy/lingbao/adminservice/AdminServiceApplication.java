package com.sundy.lingbao.adminservice;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sundy.lingbao.biz.LingbaoBizConfig;


@EnableEurekaClient
@EnableAspectJAutoProxy
@EnableTransactionManagement
@Configuration
@EnableAutoConfiguration
@PropertySource(value = {"classpath:adminservice.properties"})
@ComponentScan(basePackageClasses={
		LingbaoBizConfig.class,
		AdminServiceApplication.class,
})
public class AdminServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(AdminServiceApplication.class).run(args);
		context.addApplicationListener(new ApplicationPidFileWriter());
	}
	
}
