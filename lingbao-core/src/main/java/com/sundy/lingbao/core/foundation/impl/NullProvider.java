package com.sundy.lingbao.core.foundation.impl;

import java.io.IOException;
import java.io.InputStream;

import com.sundy.lingbao.core.foundation.spi.provider.ApplicationProvider;
import com.sundy.lingbao.core.foundation.spi.provider.NetworkProvider;
import com.sundy.lingbao.core.foundation.spi.provider.Provider;
import com.sundy.lingbao.core.foundation.spi.provider.ServerProvider;

public class NullProvider implements ApplicationProvider, NetworkProvider, ServerProvider {

	@Override
	public Class<? extends Provider> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEnvType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnvTypeSet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHostAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAppId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
	    return "(NullProvider)";
	}

	@Override
	public String getWorkerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataCenterId() {
		// TODO Auto-generated method stub
		return null;
	}

}
