package com.sundy.lingbao.portal.repository.base;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;

@Repository
public interface EnvRepository extends LingbaoGenericalRepository<EnvEntity, Long> {

	List<EnvEntity> findByEnvIdIn(Collection<String> envIds);

	EnvEntity findByEnvId(String envId);

	EnvEntity findByName(String name);

}
