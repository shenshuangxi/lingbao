package com.sundy.lingbao.portal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.dto.EnvDto;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvAppReleationEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.repository.base.EnvRepository;
import com.sundy.lingbao.portal.repository.bussiness.AppRepository;
import com.sundy.lingbao.portal.repository.bussiness.ClusterRepository;
import com.sundy.lingbao.portal.repository.bussiness.EnvAppReleationRepositoty;
import com.sundy.lingbao.portal.util.KeyUtil;

@Service
public class EnvService {
	
	@Autowired
	private EnvRepository envRepository;
	
	@Autowired
	private AppRepository appRepository;
	
	@Autowired
	private ClusterRepository clusterRepository;
	
	@Autowired
	private EnvAppReleationRepositoty envAppReleationRepositoty;
	
	public List<EnvEntity> findAll() {
		List<EnvEntity> envEntities = new ArrayList<EnvEntity>();
		Iterable<EnvEntity> iterable = envRepository.findAll();
		iterable.forEach((envEntity)->{
			envEntities.add(envEntity);
		});
		return envEntities;
	}
	
	public List<EnvEntity> findAll(Sort sort) {
		List<EnvEntity> envEntities = new ArrayList<EnvEntity>();
		Iterable<EnvEntity> iterable = envRepository.findAll(sort);
		iterable.forEach((envEntity)->{
			envEntities.add(envEntity);
		});
		return envEntities;
	}
	
	public EnvEntity createEnv(EnvDto envDto, String operator) {
		EnvEntity envEntity = envRepository.findByName(envDto.getName());
		if (Objects.nonNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the env [%s] is exists", envDto.getName()));
		}
		envEntity = BeanUtils.transfrom(EnvEntity.class, envDto);
		envEntity.setCreateBy(operator);
		envEntity.setUpdateBy(operator);
		envEntity.setEnvId(KeyUtil.getUUIDKey());
		return envRepository.save(envEntity);
	}
	
	public EnvEntity updateEnv(String envId, EnvDto envDto, String operator) {
		EnvEntity envEntity = envRepository.findByEnvId(envId);
		if (Objects.isNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the env is not exists");
		}
		envEntity.setName(envDto.getName());
		envEntity.setUrl(envDto.getUrl());
		envEntity.setState(envDto.getState());
		envEntity.setUpdateBy(operator);
		envEntity.setUpdateDate(null);
		return envRepository.save(envEntity);
	}
	
	public EnvEntity updateEnvState(String envId, Integer state, String operator) {
		EnvEntity envEntity = envRepository.findByEnvId(envId);
		if (Objects.isNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the env is not exists");
		}
		envEntity.setState(state);
		envEntity.setUpdateBy(operator);
		envEntity.setUpdateDate(null);
		return envRepository.save(envEntity);
	}
	
	public void deleteEnv(String envId) {
		EnvEntity envEntity = envRepository.findByEnvId(envId);
		if (Objects.isNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "the env is not exists");
		}
		List<EnvAppReleationEntity> envAppReleationEntities = envAppReleationRepositoty.findByEnvId(envId);
		if (Objects.nonNull(envAppReleationEntities)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("the env [%s] has cluster", envEntity.getName()));
		}
		
		
		
		envRepository.delete(envEntity);
	}

	
	public void pushApp(String appId) {
		List<EnvEntity> envEntities = new ArrayList<EnvEntity>();
		List<EnvAppReleationEntity> envAppReleationEntities = envAppReleationRepositoty.findByAppId(appId);
		if (Objects.nonNull(envAppReleationEntities) && envAppReleationEntities.size()>0) {
			List<String> envIds = envAppReleationEntities.stream().map((envAppReleationEntity)->{
				return envAppReleationEntity.getEnvId();
			}).collect(Collectors.toList());
			envEntities.addAll(envRepository.findByEnvIdIn(envIds));
		}
		if (envEntities.size()<1) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found the push env");
		}
		
	}

	public void pullApp(String appId) {
		List<EnvEntity> envEntities = new ArrayList<EnvEntity>();
		if (StringUtils.isNullOrEmpty(appId)) {
			envRepository.findAll().forEach((envEntity)->{
				envEntities.add(envEntity);
			});
		} else {
			List<EnvAppReleationEntity> envAppReleationEntities = envAppReleationRepositoty.findByAppId(appId);
			if (Objects.nonNull(envAppReleationEntities) && envAppReleationEntities.size()>0) {
				List<String> envIds = envAppReleationEntities.stream().map((envAppReleationEntity)->{
					return envAppReleationEntity.getEnvId();
				}).collect(Collectors.toList());
				envEntities.addAll(envRepository.findByEnvIdIn(envIds));
			}
		}
		
		if (envEntities.size()<1) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found the push env");
		}
	}
	
	
	public void pushCluster(String appId, String envId) {
		EnvEntity envEntity = envRepository.findByEnvId(envId);
		AppEntity appEntity = appRepository.findByAppId(appId);
		
		if (Objects.isNull(appEntity) || Objects.isNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found the env or app");
		}
		
	}

	public void pullCluster(String appId, String envId) {
		EnvEntity envEntity = envRepository.findByEnvId(envId);
		AppEntity appEntity = appRepository.findByAppId(appId);
		
		if (Objects.isNull(appEntity) || Objects.isNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found the env or app");
		}
		
	}

	

	
}
