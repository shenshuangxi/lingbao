package com.sundy.lingbao.portal.controller.bussiness;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.dto.ClusterDto;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.portal.controller.base.BaseController;
import com.sundy.lingbao.portal.entity.bussiness.ClusterEntity;
import com.sundy.lingbao.portal.service.ClusterService;

@RestController
@RequestMapping("/api/v1/app/{appId}/env/{envId}/cluster")
public class ClusterController extends BaseController {

	@Autowired
	private ClusterService clusterService;
	
	@GetMapping
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<List<ClusterDto>> findByAppIdAndEnvId(@PathVariable String appId, @PathVariable String envId) {
		List<ClusterDto> clusterDtos = new ArrayList<ClusterDto>();
		List<ClusterEntity> clusterEntities = clusterService.findByAppIdAndEnvId(appId, envId, new Sort(Direction.DESC, "updateDate"));
		if (Objects.nonNull(clusterEntities)) {
			for (ClusterEntity clusterEntity : clusterEntities) {
				clusterDtos.add(BeanUtils.transfrom(ClusterDto.class, clusterEntity));
			}
		}
		return ResponseEntity.ok(clusterDtos);
	}
	
	@GetMapping("/master")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<List<ClusterDto>> findByAppIdAndEnvIdMasterCluster(@PathVariable String appId, @PathVariable String envId) {
		List<ClusterDto> clusterDtos = new ArrayList<ClusterDto>();
		ClusterEntity clusterEntity = clusterService.findByAppIdAndEnvIdMasterCluster(appId, envId, new Sort(Direction.DESC, "updateDate"));
		if (Objects.nonNull(clusterEntity)) {
			clusterDtos.add(BeanUtils.transfrom(ClusterDto.class, clusterEntity));
		}
		return ResponseEntity.ok(clusterDtos);
	}
	
	@PutMapping("/master")
	public ResponseEntity<Object> createMaster(@PathVariable String appId, @PathVariable String envId) {
		clusterService.createMasterCluster(appId, envId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{clusterId}/children")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<List<ClusterDto>> findCluster(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId) {
		List<ClusterDto> clusterDtos = new ArrayList<ClusterDto>();
		List<ClusterEntity> clusterEntities = clusterService.findClusterChildren(appId, envId, clusterId, new Sort(Direction.DESC, "updateDate"));
		if (Objects.nonNull(clusterEntities)) {
			for (ClusterEntity clusterEntity : clusterEntities) {
				clusterDtos.add(BeanUtils.transfrom(ClusterDto.class, clusterEntity));
			}
		}
		return ResponseEntity.ok(clusterDtos);
	}
	
	@PutMapping
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<ClusterDto> create(@PathVariable String appId, @PathVariable String envId, @RequestBody ClusterDto clusterDto) {
		ClusterEntity entity = clusterService.createCluster(appId, envId, clusterDto, getAccount());
		ClusterDto dto = BeanUtils.transfrom(ClusterDto.class, entity);
		return ResponseEntity.ok(dto);
	}
	
	@PatchMapping("/{clusterId}")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<ClusterDto> update(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, @RequestBody ClusterDto clusterDto) {
		ClusterEntity entity = clusterService.updateCluster(appId, envId, clusterId, clusterDto, getAccount());
		ClusterDto dto = BeanUtils.transfrom(ClusterDto.class, entity);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{clusterId}")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, #clusterId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).DELETE) or  @permissionValidate.hasPermission(#appId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).CREATE)")
	public ResponseEntity<Object> delete(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId) {
		clusterService.deleteCluster(appId, envId, clusterId);
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/{clusterId}/file-merge")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<Object> mergeFile(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId) {
		clusterService.merge(appId, envId, clusterId, getAccount());
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/{clusterId}/file-commit")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<Object> commitFile(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, String comment) {
		clusterService.commitFile(appId, envId, clusterId, comment, getAccount());
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/{clusterId}/commitKey/{commitKey}/file-rollback")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<Object> rollbackFile(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, @PathVariable String commitKey) {
		clusterService.rollbackFile(appId, envId, clusterId, commitKey, getAccount());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{clusterId}/file-rollback")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<Object> rollbackFile(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId) {
		clusterService.rollbackFile(appId, envId, clusterId, getAccount());
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{clusterId}/file-push")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, #envId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).FIND)")
	public ResponseEntity<Object> pushFile(@PathVariable String appId,  @PathVariable String envId, @PathVariable String clusterId) {
		clusterService.pushFile(appId, envId, clusterId);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/update-permission")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).CREATE)")
	public ResponseEntity<Object> updateAppClusterPermission(@PathVariable String appId,  @PathVariable String envId, @RequestParam Long userId,  @RequestParam Boolean isSetPermission) {
		clusterService.updateAppClusterPermission(appId, envId, userId, isSetPermission, getAccount());
		return ResponseEntity.ok().build();
	}
	
}
