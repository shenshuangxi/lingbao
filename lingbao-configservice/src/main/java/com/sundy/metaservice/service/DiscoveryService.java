package com.sundy.metaservice.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.sundy.lingbao.core.consts.ServiceNameConsts;
import com.sundy.lingbao.core.tracer.Tracer;

@Service
public class DiscoveryService {

	@Autowired
	private EurekaClient eurekaClient;

	public List<InstanceInfo> getConfigServiceInstances() {
		Application application = eurekaClient.getApplication(ServiceNameConsts.LINGBAO_CONFIGSERVICE);
		if (application == null) {
			Tracer.logEvent("lingbao.EurekaDiscovery.NotFound", ServiceNameConsts.LINGBAO_CONFIGSERVICE);
		}
		return application != null ? application.getInstances() : Collections.emptyList();
	}

	public List<InstanceInfo> getAdminServiceInstances() {
		Application application = eurekaClient.getApplication(ServiceNameConsts.LINGBAO_ADMINSERVICE);
		if (application == null) {
			Tracer.logEvent("lingbao.EurekaDiscovery.NotFound", ServiceNameConsts.LINGBAO_ADMINSERVICE);
		}
		return application != null ? application.getInstances() : Collections.emptyList();
	}

}
