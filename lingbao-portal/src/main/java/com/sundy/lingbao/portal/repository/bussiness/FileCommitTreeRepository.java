package com.sundy.lingbao.portal.repository.bussiness;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.FileCommitTreeEntity;

@Repository
public interface FileCommitTreeRepository extends LingbaoGenericalRepository<FileCommitTreeEntity, Long> {

	List<FileCommitTreeEntity> findByAppId(String appId);

	List<FileCommitTreeEntity> findByAppIdAndEnvIdAndClusterId(String appId, String env, String clusterId);

	FileCommitTreeEntity findByAppIdAndEnvIdAndClusterIdAndCommitKey(String appId, String envId, String clusterId, String commitKey);

	List<FileCommitTreeEntity> findByAppIdAndEnvIdAndClusterIdAndCreateDateGreaterThan(String appId, String envId, String clusterId, Date createDate);

	FileCommitTreeEntity findFirstByAppIdAndEnvIdAndClusterIdOrderByIdDesc(String appId, String envId, String clusterId);

	Page<FileCommitTreeEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId, Pageable pageable);

	List<FileCommitTreeEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId, Sort sort);

}
