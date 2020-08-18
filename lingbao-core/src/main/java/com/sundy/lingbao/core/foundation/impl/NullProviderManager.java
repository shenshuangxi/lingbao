package com.sundy.lingbao.core.foundation.impl;

import com.sundy.lingbao.core.foundation.spi.ProviderManager;

public class NullProviderManager implements ProviderManager {
	
	public static final NullProvider provider = new NullProvider();

	@Override
	public String getProperty(String name, String defaultValue) {
		  return defaultValue;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public NullProvider provider(Class clazz) {
		  return provider;
	}

	@Override
	public String toString() {
		  return provider.toString();
	}
}
