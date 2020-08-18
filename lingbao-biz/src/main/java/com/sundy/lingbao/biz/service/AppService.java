package com.sundy.lingbao.biz.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sundy.lingbao.biz.entity.AppEntity;
import com.sundy.lingbao.biz.entity.ClusterEntity;
import com.sundy.lingbao.biz.entity.FileCommitTreeEntity;
import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.biz.entity.FileSnapshotEntity;
import com.sundy.lingbao.biz.entity.FileTreeEntity;
import com.sundy.lingbao.biz.repository.AppRepository;
import com.sundy.lingbao.biz.repository.ClusterRepository;
import com.sundy.lingbao.biz.repository.FileCommitTreeRepository;
import com.sundy.lingbao.biz.repository.FileRepository;
import com.sundy.lingbao.biz.repository.FileSnapshotRepository;
import com.sundy.lingbao.biz.repository.FileTreeRepository;
import com.sundy.lingbao.biz.repository.ReleaseMessageRepository;
import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.common.exception.BadRequestException;
import com.sundy.lingbao.common.exception.ServiceException;
import com.sundy.lingbao.common.util.BeanUtils;

@Service
public class AppService {

	@Autowired
	private AppRepository appRepository;
	
	@Autowired
	private ClusterRepository clusterRepository;
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCommitTreeRepository fileCommitTreeRepository;
	
	@Autowired
	private FileTreeRepository fileTreeRepository;
	
	@Autowired
	private FileSnapshotRepository fileSnapshotRepository;
	
	@Autowired
	private ReleaseMessageRepository releaseMessageRepository;
	
	public Page<AppEntity> findAll(Pageable pageable) {
	    Page<AppEntity> page = appRepository.findAll(pageable);
	    return page;
	}
	
	public List<AppEntity> findAll() {
	    Iterable<AppEntity> apps = appRepository.findAll();
	    return Lists.newArrayList(apps);
	}
	
	public List<AppEntity> findAll(Sort sort) {
	    Iterable<AppEntity> apps = appRepository.findAll(sort);
	    return Lists.newArrayList(apps);
	}

	public AppEntity findOne(String appId) {
	    return appRepository.findByAppId(appId);
	}

	public Page<AppEntity> findByOwner(String owner, Pageable pageable) {
		Page<AppEntity> page = appRepository.findByOwnerName(owner, pageable);
		return page;
	}

	public List<AppEntity> findByOwner(String owner, Sort sort) {
		List<AppEntity> appEntities = appRepository.findByOwnerName(owner, sort);
		return appEntities;
	}
	
	public List<AppEntity> findByAppIds(List<String> appIds) {
		List<AppEntity> appEntities = appRepository.findByAppIdIn(appIds);
		return appEntities;
	}
	
	public void saveApps(List<AppDto> appDtos) {
		for (AppDto appDto : appDtos) {
			AppEntity currentAppEntity = appRepository.findByAppId(appDto.getAppId());
			if (Objects.isNull(currentAppEntity)) {
				AppEntity appEntity = BeanUtils.transfrom(AppEntity.class, appDto);
				appRepository.save(appEntity);
			} else {
				AppEntity appEntity = BeanUtils.transfrom(AppEntity.class, appDto);
				appEntity.setId(currentAppEntity.getId());
				appRepository.save(appEntity);
			}
		}
	}
	
	public void deleteApp(String appId) {
		AppEntity appEntity = appRepository.findByAppId(appId);
		if (Objects.nonNull(appEntity)) {
			
			//删除app
			appRepository.delete(appEntity);
			
			//删除cluster
			List<ClusterEntity> clusterEntities = clusterRepository.findByAppId(appId);
			if (Objects.nonNull(clusterEntities)) {
				clusterRepository.deleteAll(clusterEntities);
			}
			
			//删除file
			List<FileEntity> fileEntities = fileRepository.findByAppId(appId);
			if (Objects.nonNull(fileEntities)) {
				fileRepository.deleteAll(fileEntities);
			}
			
			List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppId(appId);
			if (Objects.nonNull(fileCommitTreeEntities)) {
				fileCommitTreeRepository.deleteAll(fileCommitTreeEntities);
			}
			
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppId(appId);
			if (Objects.nonNull(fileTreeEntities)) {
				fileTreeRepository.deleteAll(fileTreeEntities);
			}
			
			List<FileSnapshotEntity> fileSnapshotEntities = fileSnapshotRepository.findByAppId(appId);
			if (Objects.nonNull(fileSnapshotEntities)) {
				fileSnapshotRepository.deleteAll(fileSnapshotEntities);
			}
		}
		throw new ServiceException("appId not found");
	}
	
	public AppEntity updateApp(AppDto appDto) {
		AppEntity appEntity = appRepository.findByAppId(appDto.getAppId());
		if (Objects.nonNull(appEntity)) {
			appEntity.setAppId(appDto.getAppId());
			appEntity.setCreateBy(appDto.getCreateBy());
			appEntity.setCreateDate(appDto.getCreateDate());
			appEntity.setName(appDto.getName());
			appEntity.setOrgName(appDto.getOrgName());
			appEntity.setOwnerEmail(appDto.getOwnerEmail());
			appEntity.setOwnerName(appDto.getOrgName());
			appEntity.setUpdateBy(appDto.getUpdateBy());
			appEntity.setUpdateDate(appDto.getUpdateDate());
			return appRepository.save(appEntity);
		}
		throw new BadRequestException(String.format("App not exists. AppId = %s", appEntity.getAppId()));
	}

	

	
	

}
