package com.sundy.lingbao.portal.auth.cas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;


public class LingbaoFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	private final static Logger logger = LoggerFactory.getLogger(LingbaoFilterInvocationSecurityMetadataSource.class);
	
	private FilterInvocationSecurityMetadataSource originalFilterInvocationSecurityMetadataSource;
	
	public LingbaoFilterInvocationSecurityMetadataSource() {}
	
	public LingbaoFilterInvocationSecurityMetadataSource(FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
		this.originalFilterInvocationSecurityMetadataSource = filterInvocationSecurityMetadataSource;
	}

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		logger.info("---------------------------------getAttributes-------------------------------");
		Collection<ConfigAttribute> configAttributes = new ArrayList<ConfigAttribute>();
		if (Objects.nonNull(this.originalFilterInvocationSecurityMetadataSource.getAttributes(object))) {
			configAttributes.addAll(this.originalFilterInvocationSecurityMetadataSource.getAttributes(object));
		}
        return configAttributes;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		logger.info("---------------------------------getAllConfigAttributes-------------------------------");
		Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();
        if (Objects.nonNull(this.originalFilterInvocationSecurityMetadataSource.getAllConfigAttributes())){
        	allAttributes.addAll(this.originalFilterInvocationSecurityMetadataSource.getAllConfigAttributes());
        }
        
        return allAttributes;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		logger.info("---------------------------------supports-------------------------------");
		return true;
	}

}
