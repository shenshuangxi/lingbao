package com.sundy.lingbao.client.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LingbaoConfigChangeListener {

	/**
	 * 值越小越先执行
	 * @return
	 */
	int ordered() default Integer.MAX_VALUE;
	
}
