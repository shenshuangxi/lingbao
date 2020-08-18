package com.sundy.lingbao.db.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({LingbaoDbRegister.class})
public @interface EnableLingbaoDb {

	String queryDataSourcePropertyPath();
	
	String eventDataSourcePropertyPath();
	
	String workerId();
	
	String datacenterId();
	
	String eventDir() default "event";
	
	int fileEventCount() default 0;
	
	int eventThreads() default 0;
	
}
