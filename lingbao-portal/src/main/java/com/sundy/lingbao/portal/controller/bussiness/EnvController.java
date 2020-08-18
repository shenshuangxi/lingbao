package com.sundy.lingbao.portal.controller.bussiness;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
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

import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.portal.controller.base.BaseController;
import com.sundy.lingbao.portal.dto.EnvDto;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.service.EnvService;

@RestController
@RequestMapping("/api/v1/env")
public class EnvController extends BaseController {

	@Autowired
	private EnvService envService;
	
	@GetMapping
	public ResponseEntity<List<EnvDto>> findAll() {
		List<EnvDto> envDtos = new ArrayList<EnvDto>();
		List<EnvEntity> envEntities = envService.findAll(new Sort(Direction.DESC, "updateDate"));
		if (Objects.nonNull(envEntities)) {
			for (EnvEntity envEntity : envEntities) {
				envDtos.add(BeanUtils.transfrom(EnvDto.class, envEntity));
			}
		}
		return ResponseEntity.ok(envDtos);
	}
	
	@PutMapping
	@PreAuthorize("@permissionValidate.isAdmin()")
	public ResponseEntity<EnvDto> create(@RequestBody EnvDto envDto) {
		if (Objects.isNull(envDto.getName()) || Objects.isNull(envDto.getUrl())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "env name url can not be null");
		}
		EnvEntity envEntity = envService.createEnv(envDto, getAccount());
		EnvDto dto = BeanUtils.transfrom(EnvDto.class, envEntity);
		return ResponseEntity.ok(dto);
	}
	
	@PatchMapping("/{envId}")
	@PreAuthorize("@permissionValidate.isAdmin()")
	public ResponseEntity<EnvDto> update(@PathVariable String envId, @RequestBody EnvDto envDto) {
		if (Objects.isNull(envDto.getName()) || Objects.isNull(envDto.getUrl())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "env name url can not be null");
		}
		EnvEntity envEntity = envService.updateEnv(envId, envDto, getAccount());
		EnvDto dto = BeanUtils.transfrom(EnvDto.class, envEntity);
		return ResponseEntity.ok(dto);
	}
	
	@PatchMapping("/{envId}/{state}")
	@PreAuthorize("@permissionValidate.isAdmin()")
	public ResponseEntity<EnvDto> update(@PathVariable String envId, @PathVariable Integer state) {
		EnvEntity envEntity = envService.updateEnvState(envId, state, getAccount());
		EnvDto dto = BeanUtils.transfrom(EnvDto.class, envEntity);
		return ResponseEntity.ok(dto);
	}
	
	
	@DeleteMapping("/{envId}")
	@PreAuthorize("@permissionValidate.isAdmin()")
	public ResponseEntity<Object> delete(@PathVariable String envId) {
		envService.deleteEnv(envId);
		return ResponseEntity.ok().build();
	}
	
}
