package com.sundy.lingbao.biz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.FileCommitTreeEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface FileCommitTreeRepository extends LingbaoGenericalRepository<FileCommitTreeEntity, Long> {

	List<FileCommitTreeEntity> findByAppId(String appId);

	List<FileCommitTreeEntity> findByAppIdAndClusterId(String appId, String clusterId);

	FileCommitTreeEntity findFirstByAppIdAndClusterIdOrderByIdDesc(String appId, String clusterId);

	FileCommitTreeEntity findByAppIdAndClusterIdAndCommitKey(String appId, String clusterId, String commitKey);

	List<FileCommitTreeEntity> findByAppIdAndClusterIdAndCreateDateGreaterThan(String appId, String clusterId, Date createDate);

}
