package com.sundy.lingbao.file.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	private static boolean isEmpty(String value) {
		if(value!=null){
			value = value.trim();
		}
		return StringUtils.isEmpty(value);
	}

}
