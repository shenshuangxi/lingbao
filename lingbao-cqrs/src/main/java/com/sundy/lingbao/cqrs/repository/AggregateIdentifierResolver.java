package com.sundy.lingbao.cqrs.repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import com.sundy.lingbao.core.util.ClassUtil;
import com.sundy.lingbao.cqrs.aggregate.VersionAggregateIdentifier;
import com.sundy.lingbao.cqrs.aggregate.annotation.AggregateIdentifier;
import com.sundy.lingbao.cqrs.aggregate.annotation.AggregateVersion;

public class AggregateIdentifierResolver {

	public static VersionAggregateIdentifier resolveTarget(Object target) {
		String identifier = null;
		Long version = null;
		try {
			identifier = findAggregateIdentifier(target);
			version = findAggregateVersion(target);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return new VersionAggregateIdentifier(identifier, version);
	}
	
	private static Long findAggregateVersion(Object payload) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		List<Field> fields = ClassUtil.findAllField(payload.getClass());
		for (Field field : fields) {
			if (field.isAnnotationPresent(AggregateVersion.class)) {
				if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) && !field.isAccessible()) {
					field.setAccessible(true);
				}
				return asLong(field.get(payload));
			}
		}
		List<Method> methods = ClassUtil.findAllMethods(payload.getClass());
		for (Method method : methods) {
			if (method.isAnnotationPresent(AggregateVersion.class)) {
				if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
					method.setAccessible(true);
				}
				return asLong(method.invoke(payload));
			}
		}
		return null;
	}

	private static Long asLong(Object version) {
		if (version == null) {
			return null;
		} else if (Number.class.isInstance(version)) {
			return ((Number) version).longValue();
		} else {
			return Long.parseLong(version.toString());
		}
	}

	private static String findAggregateIdentifier(Object payload) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		List<Field> fields = ClassUtil.findAllField(payload.getClass());
		for (Field field : fields) {
			if (field.isAnnotationPresent(AggregateIdentifier.class)) {
				if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) && !field.isAccessible()) {
					field.setAccessible(true);
				}
				return Optional.ofNullable(field.get(payload)).map(Object::toString).orElse(null);
			}
		}
		List<Method> methods = ClassUtil.findAllMethods(payload.getClass());
		for (Method method : methods) {
			if (method.isAnnotationPresent(AggregateIdentifier.class)) {
				if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
					method.setAccessible(true);
				}
				return Optional.ofNullable(method.invoke(payload)).map(Object::toString).orElse(null);
			}
		}
		return null;
	}
	
}
