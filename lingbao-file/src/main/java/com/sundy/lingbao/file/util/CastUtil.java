package com.sundy.lingbao.file.util;

public class CastUtil {

	public static String castString(Object obj){
		return CastUtil.castString(obj,"");
	}

	public static String castString(Object obj, String defaultValue) {
		return obj==null?defaultValue:String.valueOf(obj);
	}
	
	public static Double castDouble(Object obj){
		return CastUtil.castDouble(obj,0d);
	}

	public static Double castDouble(Object obj, Double defaultValue) {
		Double value = defaultValue;
		if(obj!=null){
			String strValue = castString(obj);
			try {
				if(StringUtil.isNotEmpty(strValue)){
					value = Double.parseDouble(strValue);
				}
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}
		return value;
	}
	
	
	public static Long castLong(Object obj){
		return CastUtil.castLong(obj,0l);
	}

	public static Long castLong(Object obj, Long defaultValue) {
		Long value = defaultValue;
		if(obj!=null){
			String strValue = castString(obj);
			try {
				if(StringUtil.isNotEmpty(strValue)){
					value = Long.parseLong(strValue);
				}
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}
		return value;
	}
	
	public static Integer castInt(Object obj){
		return CastUtil.castInt(obj,0);
	}

	public static Integer castInt(Object obj, Integer defaultValue) {
		Integer value = defaultValue;
		if(obj!=null){
			String strValue = castString(obj);
			try {
				if(StringUtil.isNotEmpty(strValue)){
					value = Integer.parseInt(strValue);
				}
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}
		return value;
	}
	
	public static Boolean castBoolean(Object obj){
		return CastUtil.castBoolean(obj,false);
	}

	public static Boolean castBoolean(Object obj, Boolean defaultValue) {
		Boolean value = defaultValue;
		if(obj!=null){
			String strValue = castString(obj);
			try {
				if(StringUtil.isNotEmpty(strValue)){
					value = Boolean.parseBoolean(strValue);
				}
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}
		return value;
	}
	
	
	
	
	
	
	
}
