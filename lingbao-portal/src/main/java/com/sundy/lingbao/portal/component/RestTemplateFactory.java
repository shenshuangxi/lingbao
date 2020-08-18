package com.sundy.lingbao.portal.component;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sundy.lingbao.portal.component.config.PortalConfig;

@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {

	private Logger logger = LoggerFactory.getLogger(RestTemplateFactory.class);
	
	@Autowired
	private HttpMessageConverters httpMessageConverters;
	
	@Autowired
	private PortalConfig portalConfig;
	
	private RestTemplate restTemplate;
	
	@Override
	public RestTemplate getObject() throws Exception {
		logger.info("get restTemplate");
		return restTemplate;
	}

	@Override
	public Class<?> getObjectType() {
		return RestTemplate.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("properties set start");
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		restTemplate = new RestTemplate(httpMessageConverters.getConverters());
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectionRequestTimeout(portalConfig.getConnectionRequestTimeout());
		requestFactory.setConnectTimeout(portalConfig.getConnectionTimeout());
		requestFactory.setBufferRequestBody(portalConfig.getBufferRequestBody());
		requestFactory.setReadTimeout(portalConfig.getReadTimeout());
		restTemplate.setRequestFactory(requestFactory);
		logger.info("properties set finish");
	}

}
