package com.sundy.lingbao.common.config;

import java.util.Map;

import org.springframework.core.env.MapPropertySource;

public abstract class RefreshablePropertySource extends MapPropertySource {

	public RefreshablePropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}
	
	protected abstract void refresh();


}
