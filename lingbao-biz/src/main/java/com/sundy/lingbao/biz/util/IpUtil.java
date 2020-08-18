package com.sundy.lingbao.biz.util;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;
import com.sundy.lingbao.core.util.StringUtils;

public class IpUtil {

	private static final Splitter X_FORWARDED_FOR_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
	
	public static String tryToGetClientIp(HttpServletRequest request) {
	    String forwardedFor = request.getHeader("X-FORWARDED-FOR");
	    if (!StringUtils.isNullOrEmpty(forwardedFor)) {
	    	return X_FORWARDED_FOR_SPLITTER.splitToList(forwardedFor).get(0);
	    }
	    return request.getRemoteAddr();
	}
	
}
