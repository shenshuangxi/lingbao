package com.sundy.lingbao.client;

import com.sundy.lingbao.client.config.Config;
import com.sundy.lingbao.core.util.ServiceBootstrap;

public class ConfigService {

	private static String appId;
	
	private static String registerServerHost;
	
	public static void setAppId(String appId) {
		ConfigService.appId = appId;
	}

	public static void setRegisterServerHost(String registerServerHost) {
		ConfigService.registerServerHost = registerServerHost;
	}
	
	private static Config m_config;

	public static Config getConfig() {
		if (m_config==null) {
			synchronized (ConfigService.class) {
				m_config = ServiceBootstrap.loadFirst(Config.class);
				m_config.setAppId(appId);
				m_config.setRegisterServerHost(registerServerHost);
			}
		}
		return m_config;
	}
	
	public static void clearConfig() {
		if (m_config!=null) {
			synchronized (ConfigService.class) {
				if (m_config!=null) {
					m_config.stop();
					m_config = null;
				}
			}
		}
	}

}
