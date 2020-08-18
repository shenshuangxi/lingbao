package com.sundy.lingbao.portal.repository.bussiness;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.FileTreeEntity;

@Repository
public interface FileTreeRepository extends LingbaoGenericalRepository<FileTreeEntity, Long> {

	List<FileTreeEntity> findByAppId(String appId);
	
	List<FileTreeEntity> findByAppIdAndEnvIdAndClusterId(String appId, String envId, String clusterId);

	List<FileTreeEntity> findByAppIdAndEnvIdAndClusterIdAndFileTreeIdIn(String appId, String envId, String clusterId, Collection<String> fileTreeIds);

}
