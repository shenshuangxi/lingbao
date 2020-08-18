package com.sundy.lingbao.biz.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;

@Repository
public interface ReleaseMessageRepository extends LingbaoGenericalRepository<ReleaseMessageEntity, Long> {

	ReleaseMessageEntity findByMessage(String message);

	List<ReleaseMessageEntity> findFirst100ByMessageAndIdLessThanOrderByIdAsc(String message, long id);

	ReleaseMessageEntity findTopByOrderByIdDesc();

	List<ReleaseMessageEntity> findFirst100ByIdGreaterThanOrderByIdAsc(long maxIdScanned);

	List<ReleaseMessageEntity> findByAppId(String appId);

	ReleaseMessageEntity findFirstByMessageOrderByIdDesc(String message);

}
