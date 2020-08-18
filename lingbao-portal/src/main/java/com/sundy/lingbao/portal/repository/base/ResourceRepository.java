package com.sundy.lingbao.portal.repository.base;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.base.ResourceEntity;

@Repository
public interface ResourceRepository extends LingbaoGenericalRepository<ResourceEntity, Long> {

	List<ResourceEntity> findByUserId(Long userId);

	List<ResourceEntity> findByKeyStartingWith(String key);
	
	List<ResourceEntity> findByKeyStartingWithAndKeyType(String key, String keyType);

	List<ResourceEntity> findByUserIdAndKeyType(Long userId, String keyType);

	List<ResourceEntity> findByKeyAndKeyType(String key, String keyType);
	
	ResourceEntity findByUserIdAndKeyAndKeyTypeAndAuthType(Long userId, String key, String keyType, Integer authType);

}
