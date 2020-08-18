package com.sundy.lingbao.demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.client.ConfigService;
import com.sundy.lingbao.client.config.Config;
import com.sundy.lingbao.client.spring.annotation.EnableLingbaoConfig;
import com.sundy.lingbao.client.spring.annotation.EnableRefreshProperties;
import com.sundy.lingbao.core.consts.GloablConst;

@EnableLingbaoConfig(appId="123456789", registerServerHost="http://192.168.119.159:8089")
@SpringBootApplication
@RestController
@EnableRefreshProperties
public class DemoSpringApplication {

	private static ConfigurableApplicationContext applicationContext;
//	private static String[] args;
	
	public static void main(String[] args) {
//		DemoSpringApplication.args = args;
		applicationContext = new SpringApplicationBuilder(DemoSpringApplication.class).run(args);
		PropertySource<?> propertySource =  applicationContext.getEnvironment().getPropertySources().get(GloablConst.LINGBAO_PROPERTY_SOURCE_NAME);
		System.out.println(propertySource);
		Config config = ConfigService.getConfig();
		System.out.println(config);
	}
	
	@RequestMapping("/")
    public ResponseEntity<Map<String,Object>> home() {
		Map<String,Object> retValue = new HashMap<String, Object>();
		PropertySource<?> propertySource =  ((ConfigurableApplicationContext)applicationContext).getEnvironment().getPropertySources().get(GloablConst.LINGBAO_PROPERTY_SOURCE_NAME);
		CompositePropertySource compositePropertySource = (CompositePropertySource) propertySource;
		Collection<PropertySource<?>> propertySources = compositePropertySource.getPropertySources();
		for (PropertySource<?> temp : propertySources) {
			Object source = temp.getSource();
			retValue.put(temp.getName(), source);
		}
        return ResponseEntity.ok(retValue);
    }
	
//	@LingbaoConfigChangeListener
//	public void configChange() {
//		if (applicationContext!=null) {
//			ConfigService.clearConfig();
//			applicationContext.close();
//			applicationContext = new SpringApplicationBuilder(DemoSpringApplication.class).run(DemoSpringApplication.args);
//		}
//	}
	
	
	
}
