package com.sundy.lingbao.adminservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.biz.entity.AppEntity;
import com.sundy.lingbao.biz.service.AppService;
import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.common.util.BeanUtils;

@RestController
@RequestMapping("/api/v1/app")
public class AppController {
	
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
	
	
	@PostMapping
	public ResponseEntity<String> saveApp(@RequestBody List<AppDto> appDtos) {
		appService.saveApps(appDtos);
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/{appId}")
	public ResponseEntity<AppDto> updateApp(@RequestBody AppDto appDto) {
		AppEntity appEntity = appService.updateApp(appDto);
		AppDto dto = BeanUtils.transfrom(AppDto.class, appEntity);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{appId}")
	public ResponseEntity<String> deleteApp(@PathVariable String appId) {
		appService.deleteApp(appId);
		return ResponseEntity.ok().build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
