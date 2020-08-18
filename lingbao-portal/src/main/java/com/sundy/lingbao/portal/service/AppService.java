package com.sundy.lingbao.portal.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.portal.api.AdminServiceAPI;
import com.sundy.lingbao.portal.auth.AuthConst.AuthType;
import com.sundy.lingbao.portal.entity.base.AccountEntity;
import com.sundy.lingbao.portal.entity.base.ResourceEntity;
import com.sundy.lingbao.portal.entity.base.UserEntity;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;
import com.sundy.lingbao.portal.entity.bussiness.ClusterEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvAppReleationEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileCommitTreeEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileSnapshotEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileTreeEntity;
import com.sundy.lingbao.portal.repository.base.AccountRepository;
import com.sundy.lingbao.portal.repository.base.EnvRepository;
import com.sundy.lingbao.portal.repository.base.ResourceRepository;
import com.sundy.lingbao.portal.repository.base.UserRepository;
import com.sundy.lingbao.portal.repository.bussiness.AppRepository;
import com.sundy.lingbao.portal.repository.bussiness.ClusterRepository;
import com.sundy.lingbao.portal.repository.bussiness.EnvAppReleationRepositoty;
import com.sundy.lingbao.portal.repository.bussiness.FileCommitTreeRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileSnapshotRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileTreeRepository;
import com.sundy.lingbao.portal.util.KeyUtil;

@Service
@Transactional
public class AppService {

	@Autowired
	private AdminServiceAPI.AppAPI appAPI;
	
	@Autowired
	private EnvRepository envRepository;
	
	@Autowired
	private EnvAppReleationRepositoty envAppReleationRepositoty;
	
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
	private UserRepository userRepository;
	
	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
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
	
	public AppEntity createApp(AppDto appDto, String operatorAccount) {
		AppEntity appEntity = appRepository.findByAppIdOrName(appDto.getAppId(), appDto.getName());
		if (Objects.isNull(appEntity)) {
			appEntity = new AppEntity();
			appEntity.setAppId(appDto.getAppId());
			appEntity.setName(appDto.getName());
			appEntity.setOrgName(appDto.getOrgName());
			appEntity.setEnvIds(appDto.getEnvIds());
			
			appReleationEnv(appEntity, Arrays.asList(appDto.getEnvIds().split(",")), operatorAccount);
			
			AccountEntity accountEntity = accountRepository.findByAccount(operatorAccount);
			Optional<UserEntity> optional = userRepository.findById(accountEntity.getUserId());
			
			if (optional.isPresent()) {
				UserEntity userEntity = optional.get();
				appEntity.setOwnerName(optional.get().getName());
				appEntity.setOwnerEmail(optional.get().getEmail());
				appEntity.setOwnerPhone(optional.get().getPhone());
				appEntity.setCreateBy(operatorAccount);
				appEntity.setUpdateBy(operatorAccount);
				resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(appDto.getAppId()), AppEntity.class.getName(), AuthType.CREATE.getType()));
				resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(appDto.getAppId()), AppEntity.class.getName(), AuthType.UPDATE.getType()));
				resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(appDto.getAppId()), AppEntity.class.getName(), AuthType.DELETE.getType()));
				resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(appDto.getAppId()), AppEntity.class.getName(), AuthType.COMMITPUSH.getType()));
				resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(appDto.getAppId()), AppEntity.class.getName(), AuthType.FIND.getType()));
			} else {
				throw new LingbaoException(HttpStatus.UNAUTHORIZED.value(), "use info not found");
			}
			appRepository.save(appEntity);
			for (String envId : Arrays.asList(appDto.getEnvIds().split(","))) {
				appAPI.save(envId, Collections.singletonList(BeanUtils.transfrom(AppDto.class, appEntity)));
			}
			return appEntity;
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("app appId [%s] or name [%s] is exists", appDto.getAppId(), appDto.getName()));
	}
	
	
	
	
	public void deleteApp(String appId) {
		AppEntity appEntity = appRepository.findByAppId(appId);
		if (Objects.nonNull(appEntity)) {
			//删除app
			appRepository.delete(appEntity);
			
			//删除权限
			List<ResourceEntity> resourceEntities = resourceRepository.findByKeyStartingWith(KeyUtil.getKey(appId));
			if (Objects.nonNull(resourceEntities)) {
				resourceRepository.deleteAll(resourceEntities);
			}
			
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
			
			List<EnvAppReleationEntity> envAppReleationEntities = envAppReleationRepositoty.findByAppId(appId);
			if (Objects.nonNull(envAppReleationEntities)) {
				envAppReleationRepositoty.deleteAll(envAppReleationEntities);
				List<EnvEntity> envEntities = envRepository.findByEnvIdIn(envAppReleationEntities.stream().map(EnvAppReleationEntity::getEnvId).collect(Collectors.toList()));
				for (EnvEntity envEntity : envEntities) {
					appAPI.delete(envEntity.getEnvId(), appId);
				}
			}
			return;
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("appId [%s] not found", appId));
	}
	
	public AppEntity updateApp(String appId, AppDto appDto, String operatorAccount) {
		AppEntity persitEntity = appRepository.findByAppId(appId);
		if (Objects.nonNull(persitEntity)) {
			List<String> originEnvIds = Arrays.asList(persitEntity.getEnvIds().split(","));
			persitEntity.setName(appDto.getName());
			persitEntity.setOrgName(appDto.getOrgName());
			persitEntity.setEnvIds(appDto.getEnvIds());
			persitEntity.setUpdateBy(operatorAccount);
			appReleationEnv(persitEntity, Arrays.asList(appDto.getEnvIds().split(",")), operatorAccount);
			appRepository.save(persitEntity);
			for (String envId : originEnvIds) {
				if (appDto.getEnvIds().indexOf(envId)==-1) {
					appAPI.delete(envId, appId);
				}
			}
			for (String envId : Arrays.asList(appDto.getEnvIds().split(","))) {
				appAPI.save(envId, Collections.singletonList(BeanUtils.transfrom(AppDto.class, persitEntity)));
			}
			return persitEntity;
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("App not exists. AppId = %s", appDto.getAppId()));
	}
	
	public void appReleationEnv(AppEntity currentAppEntity, List<String> envIds, String operatorAccount) {
		if (Objects.isNull(currentAppEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "App not exists");
		}
		
		List<EnvAppReleationEntity> envAppReleationEntities = envAppReleationRepositoty.findByAppId(currentAppEntity.getAppId());
		Map<String, EnvAppReleationEntity> envAppReleationEntityMap = envAppReleationEntities.stream().collect(Collectors.toMap(EnvAppReleationEntity::getEnvId, EnvAppReleationEntity->EnvAppReleationEntity));
		if (Objects.nonNull(envAppReleationEntities)) {
			for (EnvAppReleationEntity envAppReleationEntity : envAppReleationEntities) {
				List<ClusterEntity> clusterEntities = clusterRepository.findByAppIdAndEnvId(envAppReleationEntity.getAppId(), envAppReleationEntity.getEnvId());
				if (Objects.isNull(clusterEntities) || clusterEntities.size()<1) {
					envAppReleationRepositoty.delete(envAppReleationEntity);
					envAppReleationEntityMap.remove(envAppReleationEntity.getEnvId());
				}
			}
		}
		
		AccountEntity accountEntity = accountRepository.findByAccount(operatorAccount);
		Optional<UserEntity> optional = userRepository.findById(accountEntity.getUserId());
		
		if (optional.isPresent()) {
			UserEntity userEntity = optional.get();
			List<ResourceEntity> resourceEntities = resourceRepository.findByUserIdAndKeyType(userEntity.getId(), EnvAppReleationEntity.class.getName());
			if (Objects.nonNull(resourceEntities)) {
				resourceRepository.deleteAll(resourceEntities);
			}
			
			List<EnvEntity> envEntities =  envRepository.findByEnvIdIn(envIds);
			if (Objects.nonNull(envEntities)) {
				for (EnvEntity envEntity : envEntities) {
					if (envAppReleationEntityMap.containsKey(envEntity.getEnvId())) {
						continue;
					} else {
						EnvAppReleationEntity envAppReleationEntity = new EnvAppReleationEntity();
						envAppReleationEntity.setAppId(currentAppEntity.getAppId());
						envAppReleationEntity.setCreateBy(operatorAccount);
						envAppReleationEntity.setEnvId(envEntity.getEnvId());
						envAppReleationEntity.setUpdateBy(operatorAccount);
						envAppReleationRepositoty.save(envAppReleationEntity);
						resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(currentAppEntity.getAppId(), envEntity.getEnvId()), EnvAppReleationEntity.class.getName(), AuthType.CREATE.getType()));
						resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(currentAppEntity.getAppId(), envEntity.getEnvId()), EnvAppReleationEntity.class.getName(), AuthType.UPDATE.getType()));
						resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(currentAppEntity.getAppId(), envEntity.getEnvId()), EnvAppReleationEntity.class.getName(), AuthType.DELETE.getType()));
						resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(currentAppEntity.getAppId(), envEntity.getEnvId()), EnvAppReleationEntity.class.getName(), AuthType.COMMITPUSH.getType()));
						resourceRepository.save(new ResourceEntity(userEntity.getId(), KeyUtil.getKey(currentAppEntity.getAppId(), envEntity.getEnvId()), EnvAppReleationEntity.class.getName(), AuthType.FIND.getType()));
					}
				}
			}
		} else {
			throw new LingbaoException(HttpStatus.UNAUTHORIZED.value(), "use info not found");
		}
	}

	public List<EnvEntity> findRelationEnvs(String appId) {
		List<EnvAppReleationEntity> envAppReleationEntities = envAppReleationRepositoty.findByAppId(appId);
		if (Objects.nonNull(envAppReleationEntities) && envAppReleationEntities.size()>0) {
			List<String> envIds = envAppReleationEntities.stream().map(EnvAppReleationEntity::getEnvId).collect(Collectors.toList());
			List<EnvEntity> envEntities =  envRepository.findByEnvIdIn(envIds);
			return envEntities;
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("not found appId [%s] env", appId));
	}

	public AppEntity findByAppId(String appId) {
		AppEntity appEntity = appRepository.findByAppId(appId);
		return appEntity;
	}

}
