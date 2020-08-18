package com.sundy.lingbao.portal.component.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sundy.lingbao.common.config.RefreshableConfig;
import com.sundy.lingbao.common.config.RefreshablePropertySource;
import com.sundy.lingbao.portal.component.service.PortalPropertiesPropertySource;

@Component
public class PortalConfig extends RefreshableConfig{

	
	@Autowired
	private PortalPropertiesPropertySource portalPropertiesPropertySource;
	
	@Override
	protected List<RefreshablePropertySource> getRefreshablePropertySources() {
		return Collections.singletonList(portalPropertiesPropertySource);
	}

	public Integer getConnectionRequestTimeout() {
		return getIntProperty("lingbao.portal.connectionRequestTimeout", 60000);
	}
	
	public Integer getConnectionTimeout() {
		return getIntProperty("lingbao.portal.connectionTimeout", 60000);
	}
	
	public boolean getBufferRequestBody() {
		return getBooleanProperty("lingbao.portal.bufferRequestBody", false);
	}
	
	public Integer getReadTimeout() {
		return getIntProperty("lingbao.portal.readTimeout", 60000);
	}
	
	public Integer getServiceLocatorNormalDelay() {
		return getIntProperty("lingbao.portal.servicelocator.initialDelay", 60000);
	}
	
	public Integer getServiceLocatorFatalDelay() {
		return getIntProperty("lingbao.portal.servicelocator.Delay", 30000);
	}
	
	public String getBasepath() {
		return getValue("lingbao.portal.basepath");
	}
	
	
	public String getCasServerUrl() {
		return getValue("lingbao.portal.security.cas.casServerUrl");
	}
	
	public String getCasLogoutUrl() {
		return getValue("lingbao.portal.security.cas.logoutUrl");
	}
	
	public String getCasLoginUrl() {
		return getValue("lingbao.portal.security.cas.loginUrl");
	}
	
	public String getCasAuthentionUrl(){
		return getBasepath()+getCasAuthentionPath();
	}
	
	public boolean getCasSendRenew() {
		return getBooleanProperty("lingbao.portal.security.cas.sendNew", false);
	}
	
	public String getCasAuthentionPath(){
		return getValue("lingbao.portal.security.cas.authentionPath", "/login/cas");
	}

	public String getLogoutPath() {
		return getValue("lingbao.portal.security.logoutPath");
	}
	
	public String getLoginPath() {
		return getValue("lingbao.portal.security.loginPath");
	}

	public String getLoginPage() {
		return getValue("lingbao.portal.security.loginPage");
	}

	public String getTokenKey(String defaultValue) {
		return getValue("lingbao.portal.security.tokenKey", defaultValue);
	}

	public String getJwtHeaderName() {
		return getValue("lingbao.portal.security.jwt.header.name", "Authentication");
	}

	public String getJwtSecretKey() {
		return getValue("lingbao.portal.security.jwt.secretKey", "1234567890");
	}

	public long getJwtTtlMillis() {
		return getLongProperty("lingbao.portal.security.jwt.ttlMillis", 7200*1000);
	}

	public long getJwtRefreshMillis() {
		return getLongProperty("lingbao.portal.security.jwt.refreshMillis", 6200*1000);
	}

}
