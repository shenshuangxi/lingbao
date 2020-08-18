package com.sundy.lingbao.core.foundation.spi.provider;

import java.io.IOException;
import java.io.InputStream;

public interface ApplicationProvider extends Provider {

	public String getAppId();
	
	public String getWorkerId();
	
	public String getDataCenterId();
	
	public void initialize(InputStream in) throws IOException;
	
}
