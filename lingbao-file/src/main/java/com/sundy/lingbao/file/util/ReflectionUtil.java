package com.sundy.lingbao.file.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtil {

	private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
	
	public static Object newInstance(Class<?> clazz){
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			logger.error("new instance failure", e);
			throw new RuntimeException(e);
		}
	}
	
	public static Object invokeMethod(Object obj,Method method,Object... params){
		try {
			method.setAccessible(true);
			return method.invoke(obj, params);
		} catch (Exception e) {
			logger.error("invoke method failure", e);
			throw new RuntimeException(e);
		}
	}
	
	public static void setField(Object obj,Field field,Object value){
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			logger.error("set field failure", e);
			throw new RuntimeException(e);
		}
	}
	
	public static Object getField(Object obj,Field field){
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			logger.error("get field failure", e);
			throw new RuntimeException(e);
		}
	}
	
}
