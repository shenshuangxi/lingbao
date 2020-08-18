package com.sundy.lingbao.portal.repository.bussiness;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.FileSnapshotEntity;

@Repository
public interface FileSnapshotRepository extends LingbaoGenericalRepository<FileSnapshotEntity, Long> {

	List<FileSnapshotEntity> findByAppId(String appId);

	List<FileSnapshotEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId);

	FileSnapshotEntity findByAppIdAndEnvIdAndClusterIdAndFileKey(String appId, String envId, String clusterId, String fileKey);

}
