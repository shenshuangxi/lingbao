package com.sundy.lingbao.portal.repository.base;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.base.OrganzationEntity;

@Repository
public interface OrganzationRepositroy extends LingbaoGenericalRepository<OrganzationEntity, Long> {

	List<OrganzationEntity> findByIdIn(List<Long> organzationIds);

	List<OrganzationEntity> findByParentId(Long parentId);

	List<OrganzationEntity> findByHierarchyStartingWith(String hierarchy);

}
