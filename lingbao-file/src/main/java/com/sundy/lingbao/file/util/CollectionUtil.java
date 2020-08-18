package com.sundy.lingbao.file.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {

	public static boolean isNotEmpty(Map<Class<?>, Object> beanMap) {
		if(beanMap.isEmpty()){
			return false;
		}
		return true;
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return collection!=null && collection.isEmpty();
	}

}
