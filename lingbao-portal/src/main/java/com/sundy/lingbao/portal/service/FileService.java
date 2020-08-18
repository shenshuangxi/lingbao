package com.sundy.lingbao.portal.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.common.dto.FileDto;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.entity.bussiness.FileCommitTreeEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileEntity;
import com.sundy.lingbao.portal.repository.bussiness.FileCommitTreeRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileRepository;

@Service
public class FileService {

	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCommitTreeRepository fileCommitTreeRepository;
	
	public List<FileEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId, Pageable pageable) {
		return fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId, pageable);
	}
	
	public Page<FileCommitTreeEntity> findHistory(String appId, String envId, String clusterId, Pageable pageable) {
		Page<FileCommitTreeEntity> page = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId, pageable);
		return page;
	}
	
	public List<FileEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId, Sort sort) {
		return fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId, sort);
	}
	
	public List<FileEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId) {
		return fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
	}
	
	public FileEntity createFile(String appId, String envId, String clusterId, FileDto fileDto, String operator) {
		if (StringUtils.isNullOrEmpty(fileDto.getName())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] can not be null or empty", fileDto.getName()));
		}
		FileEntity fileEntity = fileRepository.findByAppIdAndEnvIdAndClusterIdAndName(appId, envId, clusterId, fileDto.getName());
		if (Objects.isNull(fileEntity)) {
			fileEntity = new FileEntity();
			fileEntity.setAppId(appId);
			fileEntity.setClusterId(clusterId);
			fileEntity.setEnvId(envId);
			fileEntity.setName(fileDto.getName());
			fileEntity.setContents(fileDto.getContents());
			fileEntity.setCreateBy(operator);
			fileEntity.setUpdateBy(operator);
			return fileRepository.save(fileEntity);
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] is exists", fileDto.getName()));
	}
	
	public FileEntity updateFile(String appId, String envId, String clusterId, Long id, FileDto fileDto, String operator) {
		if (StringUtils.isNullOrEmpty(fileDto.getName())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] can not be null or empty", fileDto.getName()));
		}
		FileEntity fileEntity = fileRepository.findByAppIdAndEnvIdAndClusterIdAndId(appId, envId, clusterId, id);
		if (Objects.nonNull(fileEntity)) {
			fileEntity.setContents(fileDto.getContents());
			if (!fileEntity.getName().equals(fileDto.getName())) {
				FileEntity oldFileEntity = fileRepository.findByAppIdAndEnvIdAndClusterIdAndName(appId, envId, clusterId, fileDto.getName());
				if (Objects.isNull(oldFileEntity)) {
					fileEntity.setName(fileDto.getName());
				} else {
					throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the file [%s] is exists", fileDto.getName()));
				}
			}
			fileEntity.setUpdateBy(operator);
			fileEntity.setUpdateDate(null);
			return fileRepository.save(fileEntity);
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the file is not exists");
	}
	
	public void deleteFile(String appId, String envId, String clusterId, Long id) {
		FileEntity fileEntity = fileRepository.findByAppIdAndEnvIdAndClusterIdAndId(appId, envId, clusterId, id);
		if (Objects.nonNull(fileEntity)) {
			fileRepository.delete(fileEntity);
			return;
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the file is not exists");
	}

	
	
}