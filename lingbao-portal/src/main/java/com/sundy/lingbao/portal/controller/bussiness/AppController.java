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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.portal.controller.base.BaseController;
import com.sundy.lingbao.portal.dto.EnvDto;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.service.AppService;

@RestController
@RequestMapping("/api/v1/app")
public class AppController extends BaseController{
	
	@Autowired
	private AppService appService;
	
	@GetMapping
	public ResponseEntity<List<AppDto>> findAll() {
		List<AppDto> appDtos = new ArrayList<AppDto>();
		List<AppEntity> appEntities = appService.findAll(new Sort(Direction.DESC, "id"));
		if (Objects.nonNull(appEntities)) {
			for (AppEntity appEntity : appEntities) {
				appDtos.add(BeanUtils.transfrom(AppDto.class, appEntity));
			}
		}
		return ResponseEntity.ok(appDtos);
	}
	
	@GetMapping("/{appId}")
	public ResponseEntity<List<AppDto>> find(@PathVariable String appId) {
		List<AppDto> appDtos = new ArrayList<AppDto>();
		AppEntity appEntity = appService.findByAppId(appId);
		if (Objects.nonNull(appEntity)) {
			appDtos.add(BeanUtils.transfrom(AppDto.class, appEntity));
		}
		return ResponseEntity.ok(appDtos);
	}
	
	@GetMapping("/{appId}/releation-env")
	public ResponseEntity<List<EnvDto>> findRelationEnvs(@PathVariable String appId) {
		List<EnvDto> envDtos = new ArrayList<EnvDto>();
		List<EnvEntity> envEntities = appService.findRelationEnvs(appId);
		if (Objects.nonNull(envEntities)) {
			for (EnvEntity envEntity : envEntities) {
				envDtos.add(BeanUtils.transfrom(EnvDto.class, envEntity));
			}
		}
		return ResponseEntity.ok(envDtos);
	}
	
	@PutMapping
	public ResponseEntity<AppDto> createApp(@RequestBody AppDto appDto) {
		AppEntity appEntity = appService.createApp(appDto, getAccount());
		AppDto dto = BeanUtils.transfrom(AppDto.class, appEntity);
		return ResponseEntity.ok(dto);
	}
	
	@PatchMapping("/{appId}")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).CREATE)")
	public ResponseEntity<AppDto> updateApp(@PathVariable String appId, @RequestBody AppDto appDto) {
		AppEntity appEntity = appService.updateApp(appId, appDto, getAccount());
		AppDto dto = BeanUtils.transfrom(AppDto.class, appEntity);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{appId}")
	@PreAuthorize("@permissionValidate.hasPermission(#appId, T(com.sundy.lingbao.portal.auth.AuthConst.AuthType).DELETE)")
	public ResponseEntity<Object> deleteApp(@PathVariable String appId) {
		appService.deleteApp(appId);
		return ResponseEntity.ok().build();
	}
	
	
}
