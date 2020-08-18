package com.sundy.lingbao.portal.repository.bussiness;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.EnvAppReleationEntity;

@Repository
public interface EnvAppReleationRepositoty extends LingbaoGenericalRepository<EnvAppReleationEntity, Long> {

	List<EnvAppReleationEntity> findByEnvId(String envId);

	EnvAppReleationEntity findByAppIdAndEnvId(String appId, String envId);

	List<EnvAppReleationEntity> findByAppId(String appId);

}
