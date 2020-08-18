package com.sundy.lingbao.portal.repository.bussiness;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.FileEntity;

@Repository
public interface FileRepository extends LingbaoGenericalRepository<FileEntity, Long> {

	List<FileEntity> findByAppId(String appId);

	List<FileEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId);

	FileEntity findByAppIdAndEnvIdAndClusterIdAndName(String appId, String envId, String clusterId, String fileName);

	FileEntity findByAppIdAndEnvIdAndClusterIdAndId(String appId, String envId, String clusterId, Long id);

	List<FileEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId, Pageable pageable);

	List<FileEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId, Sort sort);

}
