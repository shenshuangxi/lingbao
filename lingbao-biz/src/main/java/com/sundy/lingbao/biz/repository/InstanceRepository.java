package com.sundy.lingbao.biz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.InstanceEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface InstanceRepository extends LingbaoGenericalRepository<InstanceEntity, Long> {

	List<InstanceEntity> findByIp(String ip);

	InstanceEntity findByAppIdAndIp(String appId, String ip);
	
	List<InstanceEntity> findAll(Sort sort);

	Page<InstanceEntity> findByAppId(String appId, Pageable pageable);

	List<InstanceEntity> findByAppId(String appId, Sort sort);

	List<InstanceEntity> findByAppId(String appId);

}
