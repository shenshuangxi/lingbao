package com.sundy.lingbao.cqrs;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.sundy.lingbao.core.util.ClassUtil;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
	
	private final Long a;
	
	private long b;
    
    private AppTest(long b) {
    	this.a = b;
    }
    
    public Long getA() {
    	return a;
    }
    
    public Long getB() {
    	return b;
    }
    
    public void test() {
    	
    }
    
    public static void main(String[] args) {
    	
    	AppTest appTest = new AppTest(1l);
    	
    	String json = new Gson().toJson(appTest);
    	AppTest appTestNew = new Gson().fromJson(json, AppTest.class);
    	System.out.println(appTestNew);
		List<Method> methods = ClassUtil.findAllMethods(AppTest.class);
		System.out.println(methods);
		
		List<Constructor<?>> constructors = Arrays.asList(AppTest.class.getConstructors());
		System.out.println(constructors);
	}
}
