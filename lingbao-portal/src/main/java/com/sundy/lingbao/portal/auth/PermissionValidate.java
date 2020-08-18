package com.sundy.lingbao.portal.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;




import com.sundy.lingbao.portal.auth.AuthConst.AuthType;
import com.sundy.lingbao.portal.auth.AuthConst.RoleType;
import com.sundy.lingbao.portal.entity.base.ResourceEntity;
import com.sundy.lingbao.portal.repository.base.ResourceRepository;
import com.sundy.lingbao.portal.util.KeyUtil;



@Component("permissionValidate")
public class PermissionValidate {
	

	@Autowired
	private ResourceRepository resourceRepository;

	public boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (Objects.nonNull(authentication)) {
			for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
				if (grantedAuthority.getAuthority().equals(RoleType.Admin.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Map<String, List<ResourceEntity>> getAuthResources() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (Objects.nonNull(authentication)) {
			AppUser appUser = (AppUser) authentication.getPrincipal();
			List<ResourceEntity> resourceEntities = resourceRepository.findByUserId(appUser.getUserId());
			Map<String, List<ResourceEntity>> authResources = new HashMap<String, List<ResourceEntity>>();
			if (Objects.nonNull(resourceEntities)) {
				for (ResourceEntity resourceEntity : resourceEntities) {
					if (!authResources.containsKey(resourceEntity.getKey())) {
						authResources.put(resourceEntity.getKey(), new ArrayList<ResourceEntity>());
					}
					authResources.get(resourceEntity.getKey()).add(resourceEntity);
				}
			}
			return authResources;
		}
		return Collections.emptyMap();
	}
	
	public boolean hasEnvPermission(String envId, AuthType authType) {
		if (isAdmin()) {
			return true;
		}
		if (getAuthResources().containsKey(KeyUtil.getKey(envId))) {
			for (ResourceEntity resourceEntity : getAuthResources().get(envId)) {
				if (resourceEntity.getAuthType().equals(authType.getType())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasPermission(String appId, AuthType authType) {
		if (isAdmin()) {
			return true;
		}
		if (getAuthResources().containsKey(KeyUtil.getKey(appId))) {
			for (ResourceEntity resourceEntity : getAuthResources().get(appId)) {
				if (resourceEntity.getAuthType().equals(authType.getType())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasPermission(String appId, String envId, AuthType authType) {
		if (isAdmin()) {
			return true;
		}
		if (getAuthResources().containsKey(KeyUtil.getKey(appId, envId))) {
			for (ResourceEntity resourceEntity : getAuthResources().get(KeyUtil.getKey(appId, envId))) {
				if (resourceEntity.getAuthType().equals(authType.getType())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasPermission(String appId, String envId, String clusterId, AuthType authType) {
		if (isAdmin()) {
			return true;
		}
		if (getAuthResources().containsKey(KeyUtil.getKey(appId, envId, clusterId))) {
			for (ResourceEntity resourceEntity : getAuthResources().get(KeyUtil.getKey(appId, envId))) {
				if (resourceEntity.getAuthType().equals(authType.getType())) {
					return true;
				}
			}
		}
		return false;
	}
	
}
