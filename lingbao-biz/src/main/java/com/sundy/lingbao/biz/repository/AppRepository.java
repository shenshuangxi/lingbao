package com.sundy.lingbao.biz.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.AppEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;


@Repository
public interface AppRepository extends LingbaoGenericalRepository<AppEntity, Long> {

	AppEntity findByAppId(String appId);

	AppEntity findByName(String name);

	Page<AppEntity> findByOwnerName(String owner, Pageable pageable);

	List<AppEntity> findByOwnerName(String owner, Sort sort);

	List<AppEntity> findByAppIdIn(Collection<String> appIds);

}
