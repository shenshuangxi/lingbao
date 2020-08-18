package com.sundy.lingbao.portal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;

public class Base64Util {

	public static byte[] decode(final byte[] bytes) {  
        return Base64.decodeBase64(bytes);  
    }
	
	public static byte[] decode(final String base64) {  
        return Base64.decodeBase64(base64);  
    }
	
	public static String encode2String(final byte[] bytes) {  
        return Base64.encodeBase64String(bytes);
    }
	
	public static byte[] encode2Bytes(final byte[] bytes) {  
        return Base64.encodeBase64(bytes);
    }
	
	public static String encodeObject(Object obj) throws IOException{
		byte[] bytes = serialize(obj);
		return encode2String(bytes);
	}
	
	public static Object decodeObject(String base64) throws Exception{
		byte[] bytes = decode(base64);
		return deSerialize(bytes);
	}
	
	
	public static String encodeObjectFastJson(Object obj) throws IOException{
		byte[] bytes = serializeFastJson(obj);
		return encode2String(bytes);
	}
	
	public static Object decodeObjectFastJson(String base64) throws Exception{
		byte[] bytes = decode(base64);
		return deSerializeFastJson(bytes);
	}
	
	
	
	private static Object deSerializeFastJson(byte[] buf) throws Exception{
		String json = new String(buf, Charset.forName("UTF-8"));
		return JSON.parse(json);
	}
	
	private static byte[] serializeFastJson(Object obj) throws IOException{
		String json = JSON.toJSONString(obj);
		return json.getBytes(Charset.forName("UTF-8"));
	}
	
	
	private static byte[] serialize(Object obj) throws IOException{
		ObjectOutputStream oos = null;
		try {
			if(obj==null){
				return null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			return baos.toByteArray();
		} finally{
			if(oos!=null){
				oos.close();
			}
			oos = null;
		}
	}
	
	private static Object deSerialize(byte[] buf) throws Exception{
		ObjectInputStream ois = null;
		try {
			if(buf==null){
				return null;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} finally{
			if(ois!=null){
				ois.close();
			}
			ois = null;
		}
	}
	
}
