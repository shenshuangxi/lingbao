package com.sundy.lingbao.core.foundation.spi.provider;

import java.io.IOException;
import java.io.InputStream;

public interface ServerProvider extends Provider {

	public String getEnvType();
	
	public boolean isEnvTypeSet();
	
	public void initialize(InputStream in) throws IOException;
	
}
