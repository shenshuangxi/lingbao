package com.sundy.lingbao.portal.util;

import java.security.MessageDigest;

import org.springframework.http.HttpStatus;
import org.springframework.util.Base64Utils;

import com.sundy.lingbao.common.exception.LingbaoException;

public class MD5Util {

	public static String encoderByBase64Md5(String str){
		try {
			if(str!=null&&!str.trim().equals("")){
				//确定计算方法
			    MessageDigest md5 = MessageDigest.getInstance("MD5");
			    //加密后的字符串
			    String newstr= Base64Utils.encodeToString(md5.digest(str.getBytes("utf-8")));
			    return newstr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new LingbaoException(HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.name());
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
			e.printStackTrace();
		}
		throw new LingbaoException(HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.name());
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
	    	char high = chars[b>>4 & 0x0f];
	    	char low = chars[b & 0x0f];
	    	md5str.append(high);
	    	md5str.append(low);  
		}
	    return md5str.toString().toUpperCase();  
    }
    
    public static void main(String[] args) {
		System.out.println(MD5Util.encoderByHexMd5("123"));
	}
	
}
