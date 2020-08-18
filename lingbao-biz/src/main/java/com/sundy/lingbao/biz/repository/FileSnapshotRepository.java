package com.sundy.lingbao.biz.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.FileSnapshotEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface FileSnapshotRepository extends LingbaoGenericalRepository<FileSnapshotEntity, Long> {

	List<FileSnapshotEntity> findByAppId(String appId);

	List<FileSnapshotEntity> findByAppIdAndClusterId(String appId, String clusterId);

	FileSnapshotEntity findByAppIdAndClusterIdAndFileKey(String appId, String clusterId, String fileKey);

}
