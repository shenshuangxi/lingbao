package com.sundy.lingbao.biz.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.FileTreeEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface FileTreeRepository extends LingbaoGenericalRepository<FileTreeEntity, Long> {

	List<FileTreeEntity> findByAppId(String appId);
	
	List<FileTreeEntity> findByAppIdAndClusterId(String appId, String clusterId);

	FileTreeEntity findByAppIdAndClusterIdAndFileTreeId(String appId, String clusterId, String fileTreeId);

	List<FileTreeEntity> findByAppIdAndClusterIdAndFileTreeIdIn(String appId, String clusterId, Collection<String> childrenFileTreeIds);

}
