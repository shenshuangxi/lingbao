package com.sundy.lingbao.portal.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sundy.lingbao.core.dto.ServiceDto;
import com.sundy.lingbao.core.util.LingbaoThreadFactory;
import com.sundy.lingbao.portal.api.AdminServiceAPI;
import com.sundy.lingbao.portal.component.config.PortalConfig;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.service.EnvService;

@Component
public class AdminServiceAddressLocator {

	private final static Logger logger = LoggerFactory.getLogger(AdminServiceAddressLocator.class);
	
	private static final String ADMIN_SERVICE_URL_PATH = "/services/admin";
	
	@Autowired
	private PortalConfig portalConfig;
	
	private Map<String, List<ServiceDto>> cache = Maps.newConcurrentMap();
	
	private ScheduledExecutorService scheduledExecutorService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EnvService envService;
	
	@Autowired
	private AdminServiceAPI.HealthAPI healthAPI;
	
	@PostConstruct
	public void init() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(LingbaoThreadFactory.create("FileServiceAddressLocator", true));
		scheduledExecutorService.schedule(new RefreshAdminServiceAddressTask(), portalConfig.getServiceLocatorNormalDelay(), TimeUnit.MILLISECONDS);
	}
	
	
	private class RefreshAdminServiceAddressTask implements Runnable {
		@Override
		public void run() {
			boolean readSuccess = true;
			try {
				List<EnvEntity> envEntities = envService.findAll();
				if (Objects.nonNull(envEntities)) {
					for (EnvEntity envEntity : envEntities) {
						try {
							ServiceDto[] serviceDtos = restTemplate.getForObject(envEntity.getUrl()+ADMIN_SERVICE_URL_PATH, ServiceDto[].class);
							if (Objects.isNull(serviceDtos) || serviceDtos.length==0) {
								continue;
							}
							cache.put(envEntity.getEnvId(), Arrays.asList(serviceDtos));
						} catch (Throwable e) {
							readSuccess = false;
							e.printStackTrace();
							logger.error("file service connect error", e);
						}
					}
					
				} else {
					readSuccess = false;
					logger.error("no file-service configure");
				}
			} catch (Throwable e) {
				e.printStackTrace();
				logger.error("file-service address refresh error", e);
				readSuccess = false;
			}
			if (readSuccess) {
				AdminServiceAddressLocator.this.scheduledExecutorService.schedule(new RefreshAdminServiceAddressTask(), AdminServiceAddressLocator.this.portalConfig.getServiceLocatorNormalDelay(), TimeUnit.MILLISECONDS);
			} else {
				AdminServiceAddressLocator.this.scheduledExecutorService.schedule(new RefreshAdminServiceAddressTask(), AdminServiceAddressLocator.this.portalConfig.getServiceLocatorFatalDelay(), TimeUnit.MILLISECONDS);
			}
		}
	}
	
	public List<ServiceDto> getAdminServiceList(String envId){
		List<ServiceDto> services = cache.get(envId);
	    if (CollectionUtils.isEmpty(services)) {
	    	return Collections.emptyList();
	    }
	    List<ServiceDto> randomConfigServices = Lists.newArrayList(services);
	    Collections.shuffle(randomConfigServices);
	    return randomConfigServices;
	}
	
}
