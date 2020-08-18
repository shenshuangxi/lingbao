package com.sundy.db.store.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class Base64Util {

	public static byte[] decode(final byte[] bytes) {  
        return Base64.getDecoder().decode(bytes);  
    }
	
	public static byte[] decode(final String base64) {  
        return Base64.getDecoder().decode(base64);  
    }
	
	public static String encode(final byte[] bytes) {  
        return new String(Base64.getEncoder().encode(bytes));  
    }
	
	public static String encodeObject(Object obj) throws IOException{
		byte[] bytes = serialize(obj);
		return encode(bytes);
	}
	
	public static Object decodeObject(String base64) throws Exception{
		byte[] bytes = decode(base64);
		return deSerialize(bytes);
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
