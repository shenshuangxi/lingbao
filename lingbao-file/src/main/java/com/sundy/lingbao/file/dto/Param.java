package com.sundy.lingbao.file.dto;

import java.util.Map;

import com.sundy.lingbao.file.util.CastUtil;

public class Param {

	private Map<String, Object> paramMap;
	
	public Param(Map<String,Object> paramMap){
		this.paramMap = paramMap;
	}
	
	public Long getLong(String name){
		return CastUtil.castLong(paramMap.get(name));
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	
	
}
