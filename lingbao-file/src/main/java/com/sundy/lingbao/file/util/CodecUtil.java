package com.sundy.lingbao.file.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodecUtil {

	private static final Logger logger = LoggerFactory.getLogger(CodecUtil.class);
	
	public static String encodeURL(String source){
		String target = null;
		try {
			target = URLEncoder.encode(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("enode url failure", e);
			throw new RuntimeException(e);
		}
		return target;
	}
	
	public static String decodeURL(String source){
		String target = null;
		try {
			target = URLDecoder.decode(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("enode url failure", e);
			throw new RuntimeException(e);
		}
		return target;
	}
	
	
}
