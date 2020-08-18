package com.sundy.lingbao.file.util;

import org.apache.commons.lang3.ArrayUtils;


public class ArrayUtil {

	public static boolean isNotEmpty(Object[] objs) {
		return ArrayUtils.isNotEmpty(objs);
	}
	
	public static boolean isEmpty(Object[] objs) {
		return !ArrayUtils.isNotEmpty(objs);
	}

}
