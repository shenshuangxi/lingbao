package com.sundy.lingbao.portal.component.service;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.sundy.lingbao.common.config.RefreshablePropertySource;
import com.sundy.lingbao.core.util.ResourceUtils;

@Component
public class PortalPropertiesPropertySource extends RefreshablePropertySource {
	
	private static final Logger logger = LoggerFactory.getLogger(PortalPropertiesPropertySource.class);
	
	public PortalPropertiesPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}
	
	public PortalPropertiesPropertySource() {
		super("PropertiesConfig", Maps.newConcurrentMap());
	}

	@Override
	protected void refresh() {
		Properties properties = ResourceUtils.readConfigFile("portal-config.properties", null);
		properties.forEach((key, value)->{
			if (this.source.isEmpty()) {
				logger.info("Load config from properties : {} = {}", key, value);
			} else if (!Objects.equals(this.source.containsKey(key), value)) {
				logger.info("Load config from properties : {} = {}. Old value = {}", key, value, this.source.get(key));
			}
			this.source.put(String.valueOf(key), value);
		});
	}

}
