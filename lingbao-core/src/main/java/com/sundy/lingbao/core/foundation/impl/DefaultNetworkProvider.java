package com.sundy.lingbao.core.foundation.impl;

import com.sundy.lingbao.core.foundation.spi.provider.NetworkProvider;
import com.sundy.lingbao.core.foundation.spi.provider.Provider;

public class DefaultNetworkProvider implements NetworkProvider {

	@Override
	public Class<? extends Provider> getType() {
		return NetworkProvider.class;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		if ("host.address".equalsIgnoreCase(name)) {
			String val = getHostAddress();
			return val == null ? defaultValue : val;
	    } else if ("host.name".equalsIgnoreCase(name)) {
	    	String val = getHostName();
	    	return val == null ? defaultValue : val;
	    } else {
	    	return defaultValue;
	    }
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getHostAddress() {
		return NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
	}

	@Override
	public String getHostName() {
		return NetworkInterfaceManager.INSTANCE.getLocalHostName();
	}
	
	@Override
	public String toString() {
	    return "hostName [" + getHostName() + "] hostIP [" + getHostAddress() + "] (DefaultNetworkProvider)";
	}

}
