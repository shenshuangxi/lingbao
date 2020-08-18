package com.sundy.configservice.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.biz.service.FileService;
import com.sundy.lingbao.biz.service.InstanceService;
import com.sundy.lingbao.biz.service.ReleaseMessageService;
import com.sundy.lingbao.common.consts.Consts.ConfigFileOutputFormat;
import com.sundy.lingbao.common.exception.NotFoundException;
import com.sundy.lingbao.core.util.PropertiesUtil;

@RestController
@RequestMapping("/api/v1/configfile")
public class ConfigFileController {

	private static final Gson gson = new Gson();
	
	@Autowired
	private ReleaseMessageService releaseMessageService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private InstanceService instanceService;
	
	
	@GetMapping
	public ResponseEntity<Map<String,Map<Integer, String>>> queryConfigFiles(
			@RequestParam(required=true) String appId, 
			@RequestParam(required=true) Long releaseMessageId,
			@RequestParam(required=true) String clientIp) throws IOException {
		
		ReleaseMessageEntity releaseMessageEntity = releaseMessageService.findById(releaseMessageId);
		if (Objects.isNull(releaseMessageEntity)) {
			throw new NotFoundException(String.format("release message [%d] not found", releaseMessageId));
		}
		
		List<FileEntity> fileEntities = fileService.findAllBranchAndMasterFiles(appId, releaseMessageEntity.getClusterId());
		
		if (Objects.isNull(fileEntities)) {
			throw new NotFoundException(String.format("Could not load file with appId: %s, clusterName: %s", appId, releaseMessageEntity.getClusterId()));
		}
		
		Map<String,Map<Integer, String>> configFiles = new HashMap<>();
		for (FileEntity fileEntity : fileEntities) {
			configFiles.put(fileEntity.getName(), fileEntity.getContents());
		}
		instanceService.saveInstanceConfig(appId, releaseMessageEntity.getClusterId(), clientIp, releaseMessageEntity.getCommitKey());
		return ResponseEntity.ok(configFiles);
	}
	
	
	@SuppressWarnings("serial")
	@GetMapping("/propertes")
	public ResponseEntity<String> queryConfigAsProperties(
			@RequestParam(required=true) String appId, 
			@RequestParam(required=true) Long releaseMessageId) throws IOException {
		
		ReleaseMessageEntity releaseMessageEntity = releaseMessageService.findById(releaseMessageId);
		if (Objects.isNull(releaseMessageEntity)) {
			throw new NotFoundException(String.format("release message [%d] not found", releaseMessageId));
		}
		
		List<FileEntity> fileEntities = fileService.findAllBranchAndMasterFiles(appId, releaseMessageEntity.getClusterId());
		
		if (Objects.isNull(fileEntities)) {
			throw new NotFoundException(String.format("Could not load file with appId: %s, clusterName: %s", appId, releaseMessageEntity.getClusterId()));
		}
		
		Map<Object,Object> configs = new HashMap<>();
		for (FileEntity fileEntity : fileEntities) {
			configs.put(fileEntity.getName(), loadConfig(ConfigFileOutputFormat.PROPERTIES, fileEntity.getContents()));
		}
		
		String configurations = loadConfig(ConfigFileOutputFormat.PROPERTIES, configs);
		
		return new ResponseEntity<String>(configurations, new HttpHeaders(){{
			add("Content-Type", "text/plain;charset=UTF-8");
		}}, HttpStatus.OK);
	}
	
	@SuppressWarnings("serial")
	@GetMapping("/json")
	public ResponseEntity<String> queryConfigAsJson(
			@RequestParam(required=true) String appId, 
			@RequestParam(required=true) Long releaseMessageId) throws IOException {
		
		ReleaseMessageEntity releaseMessageEntity = releaseMessageService.findById(releaseMessageId);
		if (Objects.isNull(releaseMessageEntity)) {
			throw new NotFoundException(String.format("release message [%d] not found", releaseMessageId));
		}
		
		List<FileEntity> fileEntities = fileService.findAllBranchAndMasterFiles(appId, releaseMessageEntity.getClusterId());
		
		if (Objects.isNull(fileEntities)) {
			throw new NotFoundException(String.format("Could not load file with appId: %s, clusterName: %s", appId, releaseMessageEntity.getClusterId()));
		}
		
		Map<Object,Object> configs = new HashMap<>();
		for (FileEntity fileEntity : fileEntities) {
			configs.put(fileEntity.getName(), loadConfig(ConfigFileOutputFormat.JSON, fileEntity.getContents()));
		}
		
		String configurations = loadConfig(ConfigFileOutputFormat.JSON, configs);
		
		return new ResponseEntity<String>(configurations, new HttpHeaders(){{
			add("Content-Type", "application/json;charset=UTF-8");
		}}, HttpStatus.OK);
	}
	
	
	private String loadConfig(ConfigFileOutputFormat outputFormat, Map<?, ?> map) throws IOException {
		String result = null;
		switch (outputFormat) {
			case PROPERTIES:
				Properties properties = new Properties();
				properties.putAll(map);
				result = PropertiesUtil.toString(properties);
				break;
			case JSON:
				result = gson.toJson(map);
				break;
		}
		return result;
	}
	
	
}
