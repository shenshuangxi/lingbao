package com.sundy.db.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface CommandHandler {

	/**
	 * 值越小 越先处理
	 * @return
	 */
	int ordered() default Integer.MAX_VALUE;
	
}
