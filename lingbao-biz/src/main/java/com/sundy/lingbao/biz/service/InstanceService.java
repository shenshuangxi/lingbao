package com.sundy.lingbao.biz.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.biz.entity.InstanceConfigEntity;
import com.sundy.lingbao.biz.entity.InstanceEntity;
import com.sundy.lingbao.biz.repository.InstanceConfigRepository;
import com.sundy.lingbao.biz.repository.InstanceRepository;

@Service
public class InstanceService {

	@Autowired
	private InstanceRepository instanceRepository;
	
	@Autowired
	private InstanceConfigRepository instanceConfigRepository;

	public InstanceEntity saveInstance(String appId, String ip) {
		InstanceEntity instanceEntity = instanceRepository.findByAppIdAndIp(appId, ip);
		if (Objects.isNull(instanceEntity)) {
			instanceEntity = new InstanceEntity();
			instanceEntity.setIp(ip);
			instanceEntity.setAppId(appId);
			instanceEntity = instanceRepository.save(instanceEntity);
		}
		return instanceEntity;
	}

	public InstanceConfigEntity saveInstanceConfig(String appId, String cluster, String ip, String commitKey) {
		InstanceEntity instanceEntity = instanceRepository.findByAppIdAndIp(appId, ip);
		if (Objects.isNull(instanceEntity)) {
			instanceEntity = new InstanceEntity();
			instanceEntity.setIp(ip);
			instanceEntity.setAppId(appId);
			instanceEntity = instanceRepository.save(instanceEntity);
		}
		InstanceConfigEntity instanceConfigEntity = new InstanceConfigEntity();
		instanceConfigEntity.setAppId(appId);
		instanceConfigEntity.setCluster(cluster);
		instanceConfigEntity.setCommitKey(commitKey);
		instanceConfigEntity.setInstanceId(instanceEntity.getId());
		return instanceConfigRepository.save(instanceConfigEntity);
	}

	public Page<InstanceEntity> findInstanceAll(String appId, Pageable pageable) {
		Page<InstanceEntity> page = instanceRepository.findByAppId(appId, pageable);
		return page;
	}
	
	public List<InstanceEntity> findInstanceAll(String appId, Sort sort) {
		List<InstanceEntity> instanceEntities = instanceRepository.findByAppId(appId, sort);
		return instanceEntities;
	}
	
	public List<InstanceEntity> findInstanceAll(String appId) {
		List<InstanceEntity> instanceEntities = instanceRepository.findByAppId(appId);
		return instanceEntities;
	}

	public Page<InstanceConfigEntity> findInstanceConfigAll(String appId, String cluster, Long instanceId, Pageable pageable) {
		Page<InstanceConfigEntity> page = instanceConfigRepository.findByAppIdAndClusterAndInstanceId(appId, cluster, instanceId, pageable);
		return page;
	}

	public List<InstanceConfigEntity> findInstanceConfigAll(String appId, String cluster, Long instanceId, Sort sort) {
		List<InstanceConfigEntity> instanceConfigEntities = instanceConfigRepository.findByAppIdAndClusterAndInstanceId(appId, cluster, instanceId, sort);
		return instanceConfigEntities;
	}
	
}
