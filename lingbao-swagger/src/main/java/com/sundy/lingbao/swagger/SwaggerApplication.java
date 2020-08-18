package com.sundy.lingbao.swagger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


/**
 * Hello world!
 *
 */
@SpringBootApplication
@ComponentScan(basePackageClasses={SwaggerApplication.class})
public class SwaggerApplication {

	private static ConfigurableApplicationContext applicationContext;
	
	public static void main(String[] args) {
		applicationContext = new SpringApplicationBuilder(SwaggerApplication.class).build().run(args);
		System.out.println(applicationContext);
	}
	
}
