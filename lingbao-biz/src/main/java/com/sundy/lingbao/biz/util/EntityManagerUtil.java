package com.sundy.lingbao.biz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class EntityManagerUtil extends EntityManagerFactoryAccessor {

	private static final Logger logger = LoggerFactory.getLogger(EntityManagerUtil.class);

	public void closeEntityManager() {
		EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(getEntityManagerFactory());
		if (emHolder == null) {
			return;
		}
		logger.debug("Closing JPA EntityManager in EntityManagerUtil");
		EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
	}
	
}
