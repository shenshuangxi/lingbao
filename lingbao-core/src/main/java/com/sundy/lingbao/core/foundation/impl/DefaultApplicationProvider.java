package com.sundy.lingbao.core.foundation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.foundation.io.BOMInputStream;
import com.sundy.lingbao.core.foundation.spi.provider.ApplicationProvider;
import com.sundy.lingbao.core.foundation.spi.provider.Provider;
import com.sundy.lingbao.core.util.StringUtils;

public class DefaultApplicationProvider implements ApplicationProvider {

	private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationProvider.class);
	public static final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";
	private Properties m_appProperties = new Properties();

	private String m_appId;

	private String m_workerId;

	private String m_dataCenterId;

	@Override
	public Class<? extends Provider> getType() {
		return ApplicationProvider.class;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		return m_appProperties.getProperty(name, defaultValue);
	}

	@Override
	public String getAppId() {
		return m_appId;
	}

	@Override
	public String getWorkerId() {
		return m_workerId;
	}

	@Override
	public String getDataCenterId() {
		return m_dataCenterId;
	}

	@Override
	public void initialize() {
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
			if (in == null) {
				in = DefaultApplicationProvider.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
			}

			if (in == null) {
				logger.warn("{} not found from classpath!", APP_PROPERTIES_CLASSPATH);
			}
			initialize(in);
		} catch (Throwable ex) {
			logger.error("Initialize DefaultApplicationProvider failed.", ex);
		}

	}

	@Override
	public void initialize(InputStream in) throws IOException {
		try {
			if (in != null) {
				try {
					m_appProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
				} finally {
					in.close();
				}
			}
			initAppId();
			initWorkerId();
			initDataCenterId();
		} catch (Throwable ex) {
			logger.error("Initialize DefaultApplicationProvider failed.", ex);
		}
	}

	private void initAppId() {
		// 1. Get app.id from System Property
		m_appId = System.getProperty("appid");
		if (!StringUtils.isNullOrEmpty(m_appId)) {
			m_appId = m_appId.trim();
			logger.info("App ID is set to {} by appid property from System Property", m_appId);
			return;
		}
		// 2. Try to get app id from app.properties.
		m_appId = m_appProperties.getProperty("appid");
		if (!StringUtils.isNullOrEmpty(m_appId)) {
			m_appId = m_appId.trim();
			logger.info("App ID is set to {} by appid property from {}", m_appId, APP_PROPERTIES_CLASSPATH);
			return;
		}
		m_appId = null;
		logger.warn("appid is not available from System Property and {}. It is set to null", APP_PROPERTIES_CLASSPATH);
	}

	private void initWorkerId() {
		// 1. Get app.id from System Property
		m_workerId = System.getProperty("workerid");
		if (!StringUtils.isNullOrEmpty(m_workerId)) {
			m_workerId = m_workerId.trim();
			logger.info("Worker ID is set to {} by workerid property from System Property", m_workerId);
			return;
		}
		// 2. Try to get app id from app.properties.
		m_workerId = m_appProperties.getProperty("workerid");
		if (!StringUtils.isNullOrEmpty(m_workerId)) {
			m_workerId = m_workerId.trim();
			logger.info("Worker ID is set to {} by workerid property from {}", m_workerId, APP_PROPERTIES_CLASSPATH);
			return;
		}
		m_workerId = null;
		logger.warn("workerid is not available from System Property and {}. It is set to null", APP_PROPERTIES_CLASSPATH);
	}

	private void initDataCenterId() {
		// 1. Get app.id from System Property
		m_dataCenterId = System.getProperty("datacenterid");
		if (!StringUtils.isNullOrEmpty(m_dataCenterId)) {
			m_dataCenterId = m_dataCenterId.trim();
			logger.info("App ID is set to {} by app.id property from System Property", m_dataCenterId);
			return;
		}
		// 2. Try to get app id from app.properties.
		m_dataCenterId = m_appProperties.getProperty("datacenterid");
		if (!StringUtils.isNullOrEmpty(m_dataCenterId)) {
			m_dataCenterId = m_dataCenterId.trim();
			logger.info("DataCenter ID is set to {} by datacenterid property from {}", m_dataCenterId, APP_PROPERTIES_CLASSPATH);
			return;
		}
		m_dataCenterId = null;
		logger.warn("datacenterid is not available from System Property and {}. It is set to null", APP_PROPERTIES_CLASSPATH);
	}

	@Override
	public String toString() {
		return "appId [" + getAppId() + "] properties: " + m_appProperties + " (DefaultApplicationProvider)";
	}

}
