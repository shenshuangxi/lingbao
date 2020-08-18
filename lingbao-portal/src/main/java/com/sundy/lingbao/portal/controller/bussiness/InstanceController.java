package com.sundy.lingbao.portal.controller.bussiness;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.dto.InstanceConfigDto;
import com.sundy.lingbao.common.dto.InstanceDto;
import com.sundy.lingbao.portal.service.InstanceService;

@RestController
@RequestMapping("/api/v1/app/{appId}/env/{envId}")
public class InstanceController {

	@Autowired
	private InstanceService instanceService;
	
	@GetMapping("/instance")
	public ResponseEntity<List<InstanceDto>> findInstances(@PathVariable String appId, @PathVariable String envId) {
		List<InstanceDto> instanceDtos = instanceService.findInstanceAll(envId, appId);
		return ResponseEntity.ok(instanceDtos);
	}
	
	@GetMapping("/cluster/{cluster}/instanceconfig")
	public ResponseEntity<List<InstanceConfigDto>> findInstanceConfig(@PathVariable String appId, @PathVariable String envId, @PathVariable String cluster) {
		List<InstanceConfigDto> instanceConfigDtos = instanceService.findInstanceConfigAll(envId, appId, cluster);
		return ResponseEntity.ok(instanceConfigDtos);
	}
	
	
}
