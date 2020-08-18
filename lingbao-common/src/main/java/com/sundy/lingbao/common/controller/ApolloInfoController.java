package com.sundy.lingbao.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.core.Lingbao;
import com.sundy.lingbao.core.foundation.Foundation;


@RestController
@RequestMapping("/apollo")
public class ApolloInfoController {
	
	@RequestMapping("app")
	public String getApp() {
	    return Foundation.app().toString();
	}

	@RequestMapping("net")
	public String getNet() {
	    return Foundation.net().toString();
	}

	@RequestMapping("server")
	public String getServer() {
	    return Foundation.server().toString();
	}

	@RequestMapping("version")
	public String getVersion() {
	    return Lingbao.VERSION;
	}
	
}
