package com.sundy.lingbao.biz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.ClusterEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface ClusterRepository extends LingbaoGenericalRepository<ClusterEntity, Long> {

	Page<ClusterEntity> findByAppId(String appId, Pageable pageable);

	List<ClusterEntity> findByAppId(String appId, Sort sort);
	
	List<ClusterEntity> findByAppId(String appId);
	
	ClusterEntity findByAppIdAndClusterId(String appId, String clusterId);

	List<ClusterEntity> findByAppIdAndParentClusterId(String appId, String clusterId);

}
