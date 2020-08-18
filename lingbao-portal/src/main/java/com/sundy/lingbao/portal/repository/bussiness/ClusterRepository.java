package com.sundy.lingbao.portal.repository.bussiness;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.ClusterEntity;

@Repository
public interface ClusterRepository extends LingbaoGenericalRepository<ClusterEntity, Long> {

	List<ClusterEntity> findByAppId(String appId);

	List<ClusterEntity> findByAppIdAndEnvId(String appId, String envId);

	ClusterEntity findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId);

	List<ClusterEntity> findByAppIdAndEnvId(String appId, String envId, Sort sort);

	ClusterEntity findByAppIdAndEnvIdAndParentClusterIdIsNull(String appId, String envId);

	List<ClusterEntity> findByAppIdAndEnvIdAndParentClusterId(String appId, String envId, String parentClusterId);
	
	List<ClusterEntity> findByAppIdAndEnvIdAndParentClusterId(String appId, String envId, String parentClusterId, Sort sort);

}
