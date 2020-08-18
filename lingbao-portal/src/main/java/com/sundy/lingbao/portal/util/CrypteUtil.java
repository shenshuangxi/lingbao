package com.sundy.lingbao.portal.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CrypteUtil {

	
	public static byte[] encrypt(String content, String password) {
        try {             
            KeyGenerator kgen = KeyGenerator.getInstance("AES");  
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
            byte[] byteContent = content.getBytes("utf-8");  
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化  
            byte[] result = cipher.doFinal(byteContent);  
            return result; // 加密  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
	}
	
	public static byte[] decrypt(byte[] content, String password) {  
        try {  
            KeyGenerator kgen = KeyGenerator.getInstance("AES");  
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();  
            byte[] enCodeFormat = secretKey.getEncoded();  
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
            byte[] result = cipher.doFinal(content);  
            return result; // 加密  
        } catch (Exception e) {  
             e.printStackTrace();  
        }  
        return null;  
	}  
	
	/**将二进制转换成16进制 
	 * @param buf 
	 * @return 
	 */  
	public static String parseByte2HexStr(byte buf[]) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < buf.length; i++) {  
                String hex = Integer.toHexString(buf[i] & 0xFF);  
                if (hex.length() == 1) {  
                        hex = '0' + hex;  
                }  
                sb.append(hex.toUpperCase());  
        }  
        return sb.toString();  
	}  
	
	/**将16进制转换为二进制 
	 * @param hexStr 
	 * @return 
	 */  
	public static byte[] parseHexStr2Byte(String hexStr) {  
	        if (hexStr.length() < 1)  
	                return null;  
	        byte[] result = new byte[hexStr.length()/2];  
	        for (int i = 0;i< hexStr.length()/2; i++) {  
	                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	                result[i] = (byte) (high * 16 + low);  
	        }  
	        return result;  
	}  
	
	
	
	public static String md5Base64Encrypt(String content, String key){
		byte[] encryptResult = encrypt(content, key); 
		String encryptResultStr = parseByte2HexStr(encryptResult);  
		String encrypt = MD5Util.encoderByBase64Md5(encryptResultStr);
		return encrypt;
	}
	
	public static String base64Encrypt(String content, String key){
		byte[] encryptResult = encrypt(content, key); 
		System.out.println();
		String encryptResultStr = Base64Util.encode2String(encryptResult);  
		return encryptResultStr;
	}
	
	public static String base64Decrypt(String encryptContent, String key){
		byte[] decryptFrom = Base64Util.decode(encryptContent);  
		byte[] decryptResult = decrypt(decryptFrom,key);  
		return new String(decryptResult);
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String content = "testasdfwqeqqqqqqqqqqqqqfsadddddddddddd";  
//		String password = "1234567812345678123456781234567as@#$%dfqwerqwvxzcbgsrfgweazxcdsfgerqfghnfgbnrtyweszcvzxdfsqZScZXcawef8123456781234567812345678123456781234567812345678asdfa";  
		//加密  
		System.out.println("加密前：" + content);  
		byte[] encryptResult = encrypt(content, "123");  
		String encryptResultStr = Base64Util.encode2String(encryptResult);  
		System.out.println("加密后：" + encryptResultStr);  

		//解密  
		byte[] decryptFrom = Base64Util.decode(encryptResultStr);  
		byte[] decryptResult = decrypt(decryptFrom,"123");  
		System.out.println("解密后：" + new String(decryptResult)); 
		
	}
	
	
}
