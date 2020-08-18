package com.sundy.lingbao.core.foundation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.foundation.io.BOMInputStream;
import com.sundy.lingbao.core.foundation.spi.provider.Provider;
import com.sundy.lingbao.core.foundation.spi.provider.ServerProvider;
import com.sundy.lingbao.core.util.StringUtils;

public class DefaultServerProvider implements ServerProvider {

	private static final Logger logger = LoggerFactory.getLogger(DefaultServerProvider.class);
	private static final String SERVER_PROPERTIES = "/META-INF/server.properties";
	
	private String m_env;
	
	private Properties m_serverProperties = new Properties();
	
	@Override
	public Class<? extends Provider> getType() {
		return ServerProvider.class;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		if ("env".equalsIgnoreCase(name)) {
			String val = getEnvType();
			return val == null ? defaultValue : val;
	    } else {
	      String val = m_serverProperties.getProperty(name, defaultValue);
	      return val == null ? defaultValue : val.trim();
	    }
	}

	@Override
	public void initialize() {
		try {
			String path = SERVER_PROPERTIES;
			File file = new File(path);
			if (file.exists() && file.canRead()) {
				logger.info("Loading {}", file.getAbsolutePath());
				FileInputStream fis = new FileInputStream(file);
				initialize(fis);
				return;
			}
			logger.warn("{} does not exist or is not readable.", path);
			initialize(null);
	    } catch (Throwable ex) {
	    	logger.error("Initialize DefaultServerProvider failed.", ex);
	    }
	}

	@Override
	public String getEnvType() {
		return m_env;
	}

	@Override
	public boolean isEnvTypeSet() {
		return m_env != null;
	}

	@Override
	public void initialize(InputStream in) throws IOException {
		try {
			if (in != null) {
				try {
					m_serverProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
				} finally {
					in.close();
				}
			}
			initEnvType();
	    } catch (Throwable ex) {
	    	logger.error("Initialize DefaultServerProvider failed.", ex);
	    }
	}
	
	private void initEnvType() {
	    // 1. Try to get environment from JVM system property
	    m_env = System.getProperty("env");
	    if (!StringUtils.isNullOrEmpty(m_env)) {
	    	m_env = m_env.trim();
	    	logger.info("Environment is set to [{}] by JVM system property 'env'.", m_env);
	    	return;
	    }
	    // 2. Try to get environment from OS environment variable
	    m_env = System.getenv("ENV");
	    if (!StringUtils.isNullOrEmpty(m_env)) {
	    	m_env = m_env.trim();
	    	logger.info("Environment is set to [{}] by OS env variable 'ENV'.", m_env);
	    	return;
	    }
	    // 3. Try to get environment from file "server.properties"
	    m_env = m_serverProperties.getProperty("env");
	    if (!StringUtils.isNullOrEmpty(m_env)) {
	    	m_env = m_env.trim();
	    	logger.info("Environment is set to [{}] by property 'env' in server.properties.", m_env);
	    	return;
	    }
	    // 4. Set environment to null.
	    m_env = null;
	    logger.warn("Environment is set to null. Because it is not available in either (1) JVM system property 'env', (2) OS env variable 'ENV' nor (3) property 'env' from the properties InputStream.");
	}

}
