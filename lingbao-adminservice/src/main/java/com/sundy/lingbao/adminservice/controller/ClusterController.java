package com.sundy.lingbao.adminservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.biz.entity.ClusterEntity;
import com.sundy.lingbao.biz.service.ClusterService;
import com.sundy.lingbao.common.dto.ClusterDto;
import com.sundy.lingbao.common.util.BeanUtils;

@RestController
@RequestMapping("/api/v1/app/{appId}/cluster")
public class ClusterController {

	@Autowired
	private ClusterService clusterService;
	
	@GetMapping
	public ResponseEntity<List<ClusterDto>> findAll(@PathVariable String appId) {
		List<ClusterDto> clusterDtos = new ArrayList<ClusterDto>();
		List<ClusterEntity> clusterEntities = clusterService.findByAppId(appId);
		if (Objects.nonNull(clusterEntities)) {
			for (ClusterEntity clusterEntity : clusterEntities) {
				clusterDtos.add(BeanUtils.transfrom(ClusterDto.class, clusterEntity));
			}
		}
		return ResponseEntity.ok(clusterDtos);
	}
	
	@PutMapping
	public ResponseEntity<ClusterDto> create(@PathVariable String appId, @RequestBody ClusterDto clusterDto) {
		ClusterEntity entity = clusterService.createCluster(appId, clusterDto);
		ClusterDto dto = BeanUtils.transfrom(ClusterDto.class, entity);
		return ResponseEntity.ok(dto);
	}
	
	
	@PatchMapping("/{clusterId}")
	public ResponseEntity<String> update(@PathVariable String appId, @PathVariable String clusterId, @RequestBody ClusterDto clusterDto) {
		clusterService.updateCluster(appId, clusterId, clusterDto);
		return ResponseEntity.ok().build();
	}
	
	
	@DeleteMapping("/{clusterId}")
	public ResponseEntity<String> delete(@PathVariable String appId, @PathVariable String clusterId) {
		clusterService.deleteCluster(appId, clusterId);
		return ResponseEntity.ok().build();
	}
	
	
	
	
	
	
}
