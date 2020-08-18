package com.sundy.lingbao.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClassUtil {

	public static List<Field> findAllField(Class<?> clazz){
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		Class<?> superClass = clazz.getSuperclass();
		while(superClass!=null){
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
		return fields;
	}
	
	public static List<Field> findFieldsByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass){
		Class<?> superClass = clazz;
		List<Field> fields = new ArrayList<Field>();
		do {
			Field[] declareFields = superClass.getDeclaredFields();
			if (Objects.nonNull(declareFields)) {
				for (Field field : declareFields) {
					if (field.isAnnotationPresent(annotationClass)) {
						fields.add(field);
					}
				}
			}
			superClass = superClass.getSuperclass();
		} while (superClass!=null);
		return fields;
	}
	
	public static Object getFieldValueByAnnotation(Object obj, Class<? extends Annotation> annotationClass) {
		List<Field> fields = findFieldsByAnnotation(obj.getClass(), annotationClass);
		if (fields==null || fields.isEmpty()) {
			return null;
		}
		return AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				fields.get(0).setAccessible(true);
				try {
					return fields.get(0).get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	
	public static List<Method> findAllMethods(Class<?> clazz) {
		Class<?> superClass = clazz;
		List<Method> methods = new ArrayList<Method>();
		do {
			Method[] declareMethods = superClass.getDeclaredMethods();
			if (Objects.nonNull(declareMethods)) {
				for (Method method : declareMethods) {
					methods.add(method);
				}
			}
			superClass = superClass.getSuperclass();
		} while (superClass!=null);
		return methods;
	}
	
	public static List<Method> findMethodsByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Class<?> superClass = clazz;
		List<Method> methods = new ArrayList<Method>();
		do {
			Method[] declareMethods = superClass.getDeclaredMethods();
			if (Objects.nonNull(declareMethods)) {
				for (Method method : declareMethods) {
					if (method.isAnnotationPresent(annotationClass)) {
						methods.add(method);
					}
				}
			}
			superClass = superClass.getSuperclass();
		} while (superClass!=null);
		return methods;
	}
	
}
