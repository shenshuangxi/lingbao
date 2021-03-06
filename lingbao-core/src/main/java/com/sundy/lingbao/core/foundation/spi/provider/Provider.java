package com.sundy.lingbao.core.foundation.spi.provider;

public interface Provider {

	public Class<? extends Provider> getType();
	
	public String getProperty(String name, String defaultValue);
	
	public void initialize();
	
}
