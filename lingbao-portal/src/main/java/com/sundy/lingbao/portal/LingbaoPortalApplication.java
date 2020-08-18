package com.sundy.lingbao.portal;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sundy.lingbao.common.LingbaoCommonConfig;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableAspectJAutoProxy
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@PropertySource("classpath:portal.properties")
@ComponentScan(basePackageClasses={LingbaoCommonConfig.class, LingbaoPortalApplication.class})
public class LingbaoPortalApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(LingbaoPortalApplication.class).run(args);
		applicationContext.addApplicationListener(new ApplicationPidFileWriter());
	}
	
}
