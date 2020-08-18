package com.sundy.lingbao.adminservice.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.sundy.lingbao.biz.entity.FileCommitTreeEntity;
import com.sundy.lingbao.biz.entity.FileSnapshotEntity;
import com.sundy.lingbao.biz.entity.FileTreeEntity;
import com.sundy.lingbao.biz.service.FileCommitTreeService;
import com.sundy.lingbao.biz.service.FileSnapshotService;
import com.sundy.lingbao.biz.service.FileTreeService;
import com.sundy.lingbao.common.dto.FileCommitTreeDto;
import com.sundy.lingbao.common.dto.FileSnapshotDto;
import com.sundy.lingbao.common.dto.FileTreeDto;
import com.sundy.lingbao.common.util.BeanUtils;

@RestController
@RequestMapping("/api/v1/app/{appId}/cluster/{clusterId}/filecommittree")
public class FileCommitTreeController {

	@Autowired
	private FileCommitTreeService fileCommitTreeService;
	
	@Autowired
	private FileTreeService fileTreeService;
	
	@Autowired
	private FileSnapshotService fileSnapshotService;
	
	@GetMapping("/latest-commitkey")
	public ResponseEntity<String> findLatestCommitKey(@PathVariable String appId, @PathVariable String clusterId) {
		FileCommitTreeEntity fileCommitTreeEntity = fileCommitTreeService.findLatest(appId, clusterId);
		return ResponseEntity.ok(fileCommitTreeEntity==null?"":fileCommitTreeEntity.getCommitKey());
	}
	
	@GetMapping("/newthan-commitKey")
	public ResponseEntity<List<FileCommitTreeDto>> findNewThanCommitKey(@PathVariable String appId, @PathVariable String clusterId, String commitKey) {
		List<FileCommitTreeDto> fileCommitTreeDtos = new ArrayList<FileCommitTreeDto>();
		List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeService.findNewThanCommitKey(appId, clusterId, commitKey);
		if (Objects.nonNull(fileCommitTreeEntities)) {
			for (FileCommitTreeEntity fileCommitTreeEntity : fileCommitTreeEntities) {
				fileCommitTreeDtos.add(getFileCommitTreeDto(fileCommitTreeEntity));
			}
		}
		return ResponseEntity.ok(fileCommitTreeDtos);
	}
	
	
	@PutMapping
	public ResponseEntity<Object> create(@PathVariable String appId, @PathVariable String clusterId, @RequestBody List<FileCommitTreeDto> fileCommitTreeDtos) {
		fileCommitTreeService.createFileCommitTreeEntities(appId, clusterId, Lists.newArrayList(fileCommitTreeDtos));
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/newthan-commitkey/{commitKey}")
	public ResponseEntity<Object> deleteNewThanCommitKey(@PathVariable String appId, @PathVariable String clusterId, @PathVariable String commitKey) {
		fileCommitTreeService.deleteNewThanCommitKey(appId, clusterId, commitKey);
		return ResponseEntity.ok().build();
	}
	
	
	
	
	
	
	private FileCommitTreeDto getFileCommitTreeDto(FileCommitTreeEntity fileCommitTreeEntity) {
		FileCommitTreeDto fileCommitTreeDto = new FileCommitTreeDto();
		fileCommitTreeDto.setAppId(fileCommitTreeEntity.getAppId());
		fileCommitTreeDto.setClusterId(fileCommitTreeEntity.getClusterId());
		fileCommitTreeDto.setCreateBy(fileCommitTreeEntity.getCreateBy());
		fileCommitTreeDto.setCreateDate(fileCommitTreeEntity.getCreateDate());
		fileCommitTreeDto.setParentCommitKey(fileCommitTreeEntity.getParentCommitKey());
		fileCommitTreeDto.setCommitKey(fileCommitTreeEntity.getCommitKey());
		fileCommitTreeDto.setComment(fileCommitTreeEntity.getComment());
		Collection<FileTreeDto> fileTreeDtos = getFileTreeDtos(fileCommitTreeEntity.getAppId(), fileCommitTreeEntity.getClusterId(), fileCommitTreeEntity.getChildrenFileTreeIds());
		fileCommitTreeDto.setChildrenFileTreeDtos(fileTreeDtos);
		return fileCommitTreeDto;
	}
	
	private Collection<FileTreeDto> getFileTreeDtos(String appId, String clusterId, Collection<String> fileTreeIds) {
		List<FileTreeDto> fileTreeDtos = new ArrayList<FileTreeDto>();
		if (Objects.nonNull(fileTreeIds) && fileTreeIds.size()>0) {
			List<FileTreeEntity> fileTreeEntities = fileTreeService.findByAppIdAndClusterIdAndFileTreeIdIn(appId, clusterId, fileTreeIds);
			if (Objects.nonNull(fileTreeEntities)) {
				for (FileTreeEntity fileTreeEntity : fileTreeEntities) {
					FileTreeDto fileTreeDto = new FileTreeDto();
					fileTreeDto.setAppId(fileTreeEntity.getAppId());
					fileTreeDto.setClusterId(fileTreeEntity.getClusterId());
					fileTreeDto.setCreateBy(fileTreeEntity.getCreateBy());
					fileTreeDto.setCreateDate(fileTreeEntity.getCreateDate());
					fileTreeDto.setFileTreeId(fileTreeEntity.getFileTreeId());
					fileTreeDto.setCommitKey(fileTreeEntity.getCommitKey());
					fileTreeDto.setComment(fileTreeEntity.getComment());
					fileTreeDto.setTreeName(fileTreeEntity.getTreeName());
					FileSnapshotEntity fileSnapshotEntitiy = fileSnapshotService.findByAppIdAndClusterIdAndFileKey(appId, clusterId, fileTreeEntity.getFileKey());
					if (Objects.nonNull(fileSnapshotEntitiy)) {
						fileTreeDto.setFileSnapshotDto(BeanUtils.transfrom(FileSnapshotDto.class, fileSnapshotEntitiy));
					}
					fileTreeDtos.add(fileTreeDto);
				}
			}
		}
		return fileTreeDtos;
	}
	
}
