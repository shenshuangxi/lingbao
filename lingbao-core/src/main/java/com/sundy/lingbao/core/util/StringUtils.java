package com.sundy.lingbao.core.util;

public class StringUtils {

	public static boolean isContainEmpty(String[] args) {
		if (args!=null && args.length>0) {
			for (String arg : args) {
				if (arg==null || arg.length()==0 || arg.trim().length()==0) {
					return true;
				}
			}
		}
		return true;
	}
	
	public static boolean isNullOrEmpty(String value) {
		if (value==null || value.length()==0 || value.trim().length()==0) {
			return true;
		}
		return false;
	}

}
