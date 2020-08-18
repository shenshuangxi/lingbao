package com.sundy.lingbao.portal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.common.dto.InstanceConfigDto;
import com.sundy.lingbao.common.dto.InstanceDto;
import com.sundy.lingbao.portal.api.AdminServiceAPI;


@Service
public class InstanceService {

	@Autowired
	private AdminServiceAPI.InstanceAPI instanceAPI;

	public List<InstanceDto> findInstanceAll(String envId, String appId) {
		List<InstanceDto> instanceDtos = instanceAPI.loadAllInstance(envId, appId);
		return instanceDtos;
	}

	public List<InstanceConfigDto> findInstanceConfigAll(String envId, String appId, String cluster) {
		List<InstanceConfigDto> instanceConfigDtos = instanceAPI.loadAllInstanceConfig(envId, appId, cluster);
		return instanceConfigDtos;
	}
	
}
