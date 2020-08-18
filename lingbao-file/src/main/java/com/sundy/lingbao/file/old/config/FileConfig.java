package com.sundy.lingbao.file.old.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sundy.lingbao.common.config.RefreshableConfig;
import com.sundy.lingbao.common.config.RefreshablePropertySource;
import com.sundy.lingbao.core.foundation.Foundation;
import com.sundy.lingbao.file.old.service.FilePropertiesRefreshPropertySoruce;


@Component
public class FileConfig extends RefreshableConfig {

	@Autowired
	private FilePropertiesRefreshPropertySoruce filePropertiesRefreshPropertySoruce;
	
	
	@Override
	protected List<RefreshablePropertySource> getRefreshablePropertySources() {
		return Collections.singletonList(filePropertiesRefreshPropertySoruce);
	}

	public List<String> getFileServices() {
		String[] services = getArrayProperty("lingbao.file.services", new String[]{Foundation.net().getHostAddress()});
		return Arrays.asList(services);
	}
	
	public Integer getConnectionRequestTimeout() {
		return getIntProperty("lingbao.file.connectionRequestTimeout", 60000);
	}
	
	public Integer getConnectionTimeout() {
		return getIntProperty("lingbao.file.connectionTimeout", 60000);
	}
	
	public boolean getBufferRequestBody() {
		return getBooleanProperty("lingbao.file.bufferRequestBody", false);
	}
	
	public Integer getReadTimeout() {
		return getIntProperty("lingbao.file.readTimeout", 60000);
	}
	
	public Integer getServiceLocatorNormalDelay() {
		return getIntProperty("lingbao.file.servicelocator.initialDelay", 60000);
	}
	
	public Integer getServiceLocatorFatalDelay() {
		return getIntProperty("lingbao.file.servicelocator.Delay", 30000);
	}
	

}
