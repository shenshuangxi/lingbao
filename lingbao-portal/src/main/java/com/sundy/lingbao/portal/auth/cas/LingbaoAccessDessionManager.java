package com.sundy.lingbao.portal.auth.cas;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

public class LingbaoAccessDessionManager implements AccessDecisionManager {

	private final static Logger logger = LoggerFactory.getLogger(LingbaoAccessDessionManager.class);
	
	private AccessDecisionManager originalAccessDecisionManager;
	
	public LingbaoAccessDessionManager() {}
	
	public LingbaoAccessDessionManager(AccessDecisionManager accessDecisionManager) {
		this.originalAccessDecisionManager = accessDecisionManager;
	}

	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
		logger.info("---------------decide---------------------------");
		if (Objects.nonNull(originalAccessDecisionManager)) {
			originalAccessDecisionManager.decide(authentication, object, configAttributes);
		}
		return;
		
	}
	
	@Override
	public boolean supports(ConfigAttribute attribute) {
		logger.info("---------------supports---------------------------");
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		logger.info("---------------supports---------------------------"+clazz.getName());
		return true;
	}

}
