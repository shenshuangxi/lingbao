package com.sundy.lingbao.common.servercustomzer;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TomcatContainerCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	private static final Logger logger = LoggerFactory.getLogger(TomcatContainerCustomizer.class);
	private static final String TOMCAT_ACCEPTOR_COUNT = "server.tomcat.accept-count";
	private static final String TOMCAT_MAX_HTTP_HEADER_SIZE = "server.tomcat.maxHttpHeaderSize";
	
	@Autowired
	private Environment environment;
	
	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		if (!(factory instanceof TomcatServletWebServerFactory)) {
			return;
	    }
	    if (!environment.containsProperty(TOMCAT_ACCEPTOR_COUNT)) {
	    	return;
	    }
	    TomcatServletWebServerFactory tomcat =  (TomcatServletWebServerFactory) factory;
	    tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {
			@Override
			public void customize(Connector connector) {
				ProtocolHandler handler = connector.getProtocolHandler();
		        if (handler instanceof Http11NioProtocol) {
		        	Http11NioProtocol http = (Http11NioProtocol) handler;
		        	int acceptCount = Integer.parseInt(environment.getProperty(TOMCAT_ACCEPTOR_COUNT));
		        	http.setAcceptCount(acceptCount);
		        	logger.info("Setting tomcat accept count to {}", acceptCount);
		        	int maxHeaderSize = Integer.parseInt(environment.getProperty(TOMCAT_MAX_HTTP_HEADER_SIZE));
		        	http.setMaxHttpHeaderSize(maxHeaderSize);
		        	logger.info("Setting tomcat max http size to {}", maxHeaderSize);
		        }
			}
		});
		
	}
	
}
