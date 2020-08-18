package com.sundy.lingbao.file.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static <T> String toJson(T obj){
		String json = null;
		try {
			json = OBJECT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("convert pojo to json failure", e);
			throw new RuntimeException(e);
		}
		return json;
	}
	
	public static <T> T fromJson(String json,Class<T> type){
		T pojo;
		try {
			pojo = OBJECT_MAPPER.readValue(json, type);
		} catch (IOException e) {
			logger.error("convert json to pojo failure", e);
			throw new RuntimeException(e);
		}
		return pojo;
	}
	
}
