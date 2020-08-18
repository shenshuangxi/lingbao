package com.sundy.lingbao.portal.repository.bussiness;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.bussiness.ErrorEntity;

@Repository
public interface ErrorResponsitory extends LingbaoGenericalRepository<ErrorEntity, Long> {

	ErrorEntity findByKey(String key);

}
