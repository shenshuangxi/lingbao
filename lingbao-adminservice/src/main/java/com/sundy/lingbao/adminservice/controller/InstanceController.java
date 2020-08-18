package com.sundy.lingbao.adminservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.biz.entity.InstanceConfigEntity;
import com.sundy.lingbao.biz.entity.InstanceEntity;
import com.sundy.lingbao.biz.service.InstanceService;
import com.sundy.lingbao.common.dto.InstanceConfigDto;
import com.sundy.lingbao.common.dto.InstanceDto;
import com.sundy.lingbao.common.util.BeanUtils;


@RestController
@RequestMapping("/api/v1/app/{appId}")
public class InstanceController {

	@Autowired
	private InstanceService instanceService;
	
	@GetMapping("/instance")
	public ResponseEntity<List<InstanceDto>> findInstances(@PathVariable String appId, Integer pn, Integer ps) {
		List<InstanceDto> instanceDtos = new ArrayList<InstanceDto>();
		List<InstanceEntity> instanceEntities = instanceService.findInstanceAll(appId);
		if (Objects.nonNull(instanceEntities)) {
			for (InstanceEntity instanceEntity : instanceEntities) {
				instanceDtos.add(BeanUtils.transfrom(InstanceDto.class, instanceEntity));
			}
		}
		return ResponseEntity.ok(instanceDtos);
	}
	
	@GetMapping("/cluster/{cluster}/instance/{instanceId}/instanceconfig")
	public ResponseEntity<List<InstanceConfigDto>> findInstanceConfig(@PathVariable String appId, @PathVariable String cluster, @PathVariable Long instanceId, Integer pn, Integer ps) {
		List<InstanceConfigDto> instanceConfigDtos = new ArrayList<InstanceConfigDto>();
		List<InstanceConfigEntity> instanceConfigEntities = instanceService.findInstanceConfigAll(appId, cluster, instanceId, new Sort(Direction.DESC, "id"));
		if (Objects.nonNull(instanceConfigEntities)) {
			for (InstanceConfigEntity instanceConfigEntity : instanceConfigEntities) {
				instanceConfigDtos.add(BeanUtils.transfrom(InstanceConfigDto.class, instanceConfigEntity));
			}
		}
		return ResponseEntity.ok(instanceConfigDtos);
	}
	
	
}
