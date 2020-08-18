package com.sundy.metaservice.controller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.sundy.metaservice.dto.ServiceDTO;
import com.sundy.metaservice.service.DiscoveryService;

@RestController
@RequestMapping("/services")
public class ServiceController {

	@Autowired
	private DiscoveryService discoveryService;

	@RequestMapping("/config")
	public List<ServiceDTO> getConfigService() {
		List<InstanceInfo> instances = discoveryService.getConfigServiceInstances();
		List<ServiceDTO> result = instances.stream().map(new Function<InstanceInfo, ServiceDTO>() {
					@Override
					public ServiceDTO apply(InstanceInfo instance) {
						ServiceDTO service = new ServiceDTO();
						service.setAppName(instance.getAppName());
						service.setInstanceId(instance.getInstanceId());
						service.setHomepageUrl(instance.getHomePageUrl());
						return service;
					}
				}).collect(Collectors.toList());
		return result;
	}

	@RequestMapping("/admin")
	public List<ServiceDTO> getAdminService() {
		List<InstanceInfo> instances = discoveryService.getAdminServiceInstances();
		List<ServiceDTO> result = instances.stream().map(new Function<InstanceInfo, ServiceDTO>() {
					@Override
					public ServiceDTO apply(InstanceInfo instance) {
						ServiceDTO service = new ServiceDTO();
						service.setAppName(instance.getAppName());
						service.setInstanceId(instance.getInstanceId());
						service.setHomepageUrl(instance.getHomePageUrl());
						return service;
					}
				}).collect(Collectors.toList());
		return result;
	}

}
