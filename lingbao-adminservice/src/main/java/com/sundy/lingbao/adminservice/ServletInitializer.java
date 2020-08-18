package com.sundy.lingbao.adminservice;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		  System.out.println(application);
		  return application.sources(AdminServiceApplication.class);
	  }
	
}
