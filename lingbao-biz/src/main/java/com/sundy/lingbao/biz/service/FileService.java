package com.sundy.lingbao.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.biz.entity.ClusterEntity;
import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.biz.repository.ClusterRepository;
import com.sundy.lingbao.biz.repository.FileCommitTreeRepository;
import com.sundy.lingbao.biz.repository.FileRepository;
import com.sundy.lingbao.biz.repository.FileSnapshotRepository;
import com.sundy.lingbao.biz.repository.FileTreeRepository;
import com.sundy.lingbao.common.dto.FileDto;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.core.util.StringUtils;

@Service
public class FileService {

	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCommitTreeRepository fileCommitTreeRepository;
	
	@Autowired
	private FileTreeRepository fileTreeRepository;
	
	@Autowired
	private FileSnapshotRepository fileSnapshotRepository;
	
	@Autowired
	private ClusterRepository clusterRepository;
	
	public List<FileEntity> findByAppIdAndClusterId(String appId, String clusterId, Pageable pageable) {
		return fileRepository.findByAppIdAndClusterId(appId, clusterId, pageable);
	}
	
	public List<FileEntity> findByAppIdAndClusterId(String appId, String clusterId, Sort sort) {
		return fileRepository.findByAppIdAndClusterId(appId, clusterId, sort);
	}
	
	public List<FileEntity> findByAppIdAndClusterId(String appId, String clusterId) {
		return fileRepository.findByAppIdAndClusterId(appId, clusterId);
	}
	
	public FileEntity createFile(String appId, String clusterId, FileDto fileDto, String operator) {
		if (StringUtils.isNullOrEmpty(fileDto.getName())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] can not be null or empty", fileDto.getName()));
		}
		FileEntity fileEntity = fileRepository.findByAppIdAndClusterIdAndName(appId, clusterId, fileDto.getName());
		if (Objects.isNull(fileEntity)) {
			fileEntity = new FileEntity();
			fileEntity.setAppId(appId);
			fileEntity.setClusterId(clusterId);
			fileEntity.setCreateBy(operator);
			fileEntity.setName(fileDto.getName());
			return fileRepository.save(fileEntity);
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] is exists", fileDto.getName()));
	}
	
	public FileEntity updateFile(String appId, String clusterId, Long id, FileDto fileDto, String operator) {
		if (StringUtils.isNullOrEmpty(fileDto.getName())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] can not be null or empty", fileDto.getName()));
		}
		FileEntity fileEntity = fileRepository.findByAppIdAndClusterIdAndId(fileDto.getAppId(), fileDto.getClusterId(), fileDto.getId());
		if (Objects.nonNull(fileEntity)) {
			fileEntity.setContents(fileDto.getContents());
			if (!fileEntity.getName().equals(fileDto.getName())) {
				FileEntity oldFileEntity = fileRepository.findByAppIdAndClusterIdAndName(appId, clusterId, fileDto.getName());
				if (Objects.isNull(oldFileEntity)) {
					fileEntity.setName(fileDto.getName());
				} else {
					throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] is exists", fileDto.getName()));
				}
			}
			fileEntity.setUpdateBy(operator);
			return fileRepository.save(fileEntity);
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the file is not exists");
	}
	
	public void deleteFile(String appId, String clusterId, Long id) {
		FileEntity fileEntity = fileRepository.findByAppIdAndClusterIdAndId(appId, clusterId, id);
		if (Objects.nonNull(fileEntity)) {
			fileRepository.delete(fileEntity);
			return;
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the file is not exists");
	}
	
	public List<FileEntity> findAllBranchAndMasterFiles(String appId, String clusterId) {
		List<FileEntity> allFileEntities = new ArrayList<>();
		List<FileEntity> fileEntities = fileRepository.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.nonNull(fileEntities)) {
			allFileEntities.addAll(fileEntities);
		}
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndClusterId(appId, clusterId);
		if (StringUtils.isNullOrEmpty(clusterEntity.getParentClusterId())) {
			ClusterEntity parentCluster = clusterRepository.findByAppIdAndClusterId(appId, clusterEntity.getParentClusterId());
			if (Objects.nonNull(parentCluster)) {
				List<FileEntity> parentFileEntities = fileRepository.findByAppIdAndClusterId(appId, parentCluster.getClusterId());
				if (Objects.nonNull(parentFileEntities)) {
					Map<String, FileEntity> map = parentFileEntities.stream().collect(Collectors.toMap(FileEntity::getName, FileEntity->FileEntity));
					for (FileEntity fileEntity : allFileEntities) {
						if (map.containsKey(fileEntity.getName())) {
							allFileEntities.remove(fileEntity);
							Map<Integer, String> parentContents = map.get(fileEntity.getName()).getContents();
							Map<Integer, String> childContents = fileEntity.getContents();
							Integer lineNum = 1;
							for (Integer parentLineNum : parentContents.keySet()) {
								if (parentLineNum>lineNum) {
									lineNum = parentLineNum;
								}
							}
							lineNum += 1;
							for (String value : childContents.values()) {
								parentContents.put(lineNum++, value);
							}
						}
					}
					allFileEntities.addAll(parentFileEntities);
				}
			}
		}
		return allFileEntities;
	}

	
	
}