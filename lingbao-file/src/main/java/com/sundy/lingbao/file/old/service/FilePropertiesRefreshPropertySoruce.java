package com.sundy.lingbao.file.old.service;

import java.util.Map;



import java.util.Properties;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.sundy.lingbao.common.config.RefreshablePropertySource;
import com.sundy.lingbao.core.util.ResourceUtils;

@Component
public class FilePropertiesRefreshPropertySoruce extends RefreshablePropertySource {

	public FilePropertiesRefreshPropertySoruce(String name, Map<String, Object> source) {
		super(name, source);
	}
	
	public FilePropertiesRefreshPropertySoruce() {
		super("FileProperties", Maps.newConcurrentMap());
	}
	
	@Override
	protected void refresh() {
		Properties properties = ResourceUtils.readConfigFile("lingbao-file.properties", null);
		properties.forEach((key, value)->{
			FilePropertiesRefreshPropertySoruce.this.source.put(key.toString(), value);
		});
	}

}
