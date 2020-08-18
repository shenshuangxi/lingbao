package com.sundy.lingbao.core.cqrs.annotations.saga;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sundy.lingbao.core.cqrs.annotations.aggregate.EventHandler;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventHandler
public @interface SagaEventHandler {

	/**
	 * 用于寻找EventHandler对应的saga
	 * @return
	 */
	String associationProperty();
	
}
