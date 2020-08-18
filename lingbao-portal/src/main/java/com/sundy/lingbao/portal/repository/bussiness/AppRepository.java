package com.sundy.lingbao.portal.repository.bussiness;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;

@Repository
public interface AppRepository extends LingbaoGenericalRepository<AppEntity, Long> {

	AppEntity findByAppId(String appId);

	AppEntity findByName(String name);

	Page<AppEntity> findByOwnerName(String owner, Pageable pageable);

	List<AppEntity> findByOwnerName(String owner, Sort sort);

	AppEntity findByAppIdOrName(String appId, String name);

	List<AppEntity> findByAppIdIn(List<String> appIds);

	List<AppEntity> findByAppIdNotIn(List<String> appIds);

}
