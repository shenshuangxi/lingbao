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

import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.biz.service.FileService;
import com.sundy.lingbao.common.dto.FileDto;
import com.sundy.lingbao.common.util.BeanUtils;


@RestController
@RequestMapping("/api/v1/app/{appId}/cluster/{cluster}/file")
public class FileController {

	@Autowired
	private FileService fileService;
	
	@GetMapping
	public ResponseEntity<List<FileDto>> findAll(@PathVariable String appId, @PathVariable String clusterId) {
		List<FileDto> fileDtos = new ArrayList<FileDto>();
		List<FileEntity> fileEntities = fileService.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.nonNull(fileEntities)) {
			for (FileEntity fileEntity : fileEntities) {
				fileDtos.add(BeanUtils.transfrom(FileDto.class, fileEntity));
			}
		}
		return ResponseEntity.ok(fileDtos);
	}
	
	@PutMapping
	public  ResponseEntity<FileDto> create(@PathVariable String appId, @PathVariable String clusterId, @RequestBody FileDto fileDto, String operator) {
		FileEntity fileEntity = fileService.createFile(appId, clusterId, fileDto, operator);
		FileDto dto = BeanUtils.transfrom(FileDto.class, fileEntity);
		return ResponseEntity.ok(dto);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<Object> update(@PathVariable String appId, @PathVariable String clusterId, @PathVariable Long id, @RequestBody FileDto fileDto, String operator) {
		fileService.updateFile(appId, clusterId, id, fileDto, operator);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable String appId, @PathVariable String clusterId, Long id) {
		fileService.deleteFile(appId, clusterId, id);
		return ResponseEntity.ok().build();
	}
	
}
