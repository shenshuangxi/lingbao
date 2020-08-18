package com.sundy.lingbao.core.consts;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sundy.lingbao.core.util.ResourceUtils;

/**
 * The meta domain will load the meta server from System environment first, if
 * not exist, will load from apollo-env.properties. If neither exists, will load
 * the default meta url.
 * 
 * Currently, apollo supports local/dev/fat/uat/lpt/pro environments.
 */
public class MetaDomainConsts {

	private static Map<String, Object> domains = new HashMap<>();

	static {
		Properties prop = ResourceUtils.readConfigFile("lingbao-env.properties", null);
		prop.forEach((key, value) -> {
			domains.put(String.valueOf(key), value);
		});
	}

	public static String getDomain(String env) {
		return String.valueOf(domains.get(env));
	}
	
	public static Set<String> getAllEnv() {
		return domains.keySet();
	}
	
	
}
