package com.sundy.lingbao.portal.util;

import java.util.Map;
import java.util.Map.Entry;

public class Util {

	public static String mapToParamsString(Map<String,Object> params) {
		if (params!=null && params.size()>0) {
			StringBuilder sb = new StringBuilder();
			for (Entry<String, Object> entry : params.entrySet()) {
				if (entry.getKey()!=null && entry.getValue()!=null) {
					sb.append(entry.getKey()+"="+entry.getValue()+"&");
				}
			}
			sb = sb.insert(0, "?");
			return sb.substring(0, sb.length()-1);
		} 
		return "";
	}
	
}
