package com.sundy.lingbao.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Util {

	private final static Logger logger = LoggerFactory.getLogger(MD5Util.class);
	
	public static String encoderByBase64Md5(String str){
		try {
			if(str!=null&&!str.trim().equals("")){
				//确定计算方法
			    MessageDigest md5 = MessageDigest.getInstance("MD5");
			    //加密后的字符串
			    String newstr= new String(Base64.getEncoder().encode(md5.digest(str.getBytes("utf-8"))), StandardCharsets.UTF_8);
			    return newstr;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
    }
	
	
	public static String encoderByHexMd5(String str){
		try {
			if(str!=null&&!str.trim().equals("")){
				// 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象  
		        MessageDigest md5 = MessageDigest.getInstance("MD5");  
		        // 2 将消息变成byte数组  
		        byte[] input = str.getBytes("utf-8");  
		        // 3 计算后获得字节数组,这就是那128位了  
		        byte[] buff = md5.digest(input);  
			    // 4 加密后的字符串
			    String md5str = bytesToHex(buff);  
			    return md5str;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
    }
	
	
	public static String encoderByHexMd5(byte[] bytes){
		try {
			if(bytes!=null && bytes.length>0){
				// 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象  
		        MessageDigest md5 = MessageDigest.getInstance("MD5");  
		        // 3 计算后获得字节数组,这就是那128位了  
		        byte[] buff = md5.digest(bytes);  
			    // 4 加密后的字符串
			    String md5str = bytesToHex(buff);  
			    return md5str;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
    }
	
	/** 
     * 二进制转十六进制 
     *  
     * @param bytes 
     * @return 
     */  
    public static String bytesToHex(byte[] bytes) { 
    	char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	    StringBuffer md5str = new StringBuffer();  
	    for (byte b : bytes) {
	    	char high = chars[b>>>4 & 0xf];
	    	char low = chars[b & 0xf];
	    	md5str.append(high).append(low);  
		}
	    return md5str.toString().toUpperCase();  
    }
	
    
    public static void main(String[] args) {
		System.out.println(MD5Util.encoderByHexMd5("123"));
	}
}
