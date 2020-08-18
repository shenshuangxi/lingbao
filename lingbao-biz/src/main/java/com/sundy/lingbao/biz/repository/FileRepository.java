package com.sundy.lingbao.biz.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface FileRepository extends LingbaoGenericalRepository<FileEntity, Long> {

	List<FileEntity> findByAppId(String appId);

	List<FileEntity> findByAppIdAndClusterId(String appId, String clusterId);

	List<FileEntity> findByAppIdAndClusterId(String appId, String clusterId, Pageable pageable);

	List<FileEntity> findByAppIdAndClusterId(String appId, String clusterId, Sort sort);

	FileEntity findByAppIdAndClusterIdAndName(String appId, String clusterId, String fileName);

	FileEntity findByAppIdAndClusterIdAndId(String appId, String clusterId, Long id);

}
