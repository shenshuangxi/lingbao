package com.sundy.lingbao.portal.repository.base;

import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.base.AccountEntity;

@Repository
public interface AccountRepository extends LingbaoGenericalRepository<AccountEntity, Long> {

	AccountEntity findByAccountAndPassword(String account, String encoderByHexMd5);

	AccountEntity findByAccount(String account);

	AccountEntity findByGlobalId(Long globalId);

}
