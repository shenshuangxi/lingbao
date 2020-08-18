package com.sundy.lingbao.biz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.InstanceConfigEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface InstanceConfigRepository extends LingbaoGenericalRepository<InstanceConfigEntity, Long> {

	InstanceConfigEntity findFirstByInstanceIdOrderByIdDesc(Long id);

	InstanceConfigEntity findFirstByInstanceIdAndAppIdAndClusterOrderByIdDesc(Long id, String appId, String cluster);

	Page<InstanceConfigEntity> findByInstanceId(Long instanceId, Pageable pageable);

	List<InstanceConfigEntity> findByInstanceId(Long instanceId, Sort sort);

	Page<InstanceConfigEntity> findByAppIdAndClusterAndInstanceId(String appId, String cluster, Long instanceId, Pageable pageable);

	List<InstanceConfigEntity> findByAppIdAndClusterAndInstanceId(String appId, String cluster, Long instanceId, Sort sort);

}
