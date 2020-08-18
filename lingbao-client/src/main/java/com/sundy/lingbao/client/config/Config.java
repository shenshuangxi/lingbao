package com.sundy.lingbao.client.config;

import java.util.List;

import com.sundy.lingbao.client.listeners.ConfigChangeListener;

public interface Config {

	String getAppId();
	
	void setAppId(String appId);
	
	String getRegisterServerHost();
	
	void setRegisterServerHost(String registerServerHost);
	
	public List<String> getLocations();

	public void register(ConfigChangeListener configChangeListener);
	
	public void stop();
	
}
