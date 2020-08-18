package com.sundy.lingbao.core.util;

import java.util.Iterator;
import java.util.ServiceLoader;


public class ServiceBootstrap {

	public static <T> T loadFirst(Class<T> clazz) {
		Iterator<T> iterator = loadAll(clazz);
		if (iterator.hasNext()) {
			return iterator.next();
		}
		throw new IllegalStateException(String.format("No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!", clazz.getName()));
	}
	
	public static <T> Iterator<T> loadAll(Class<T> clazz) {
		ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
		return serviceLoader.iterator();
	}

}
