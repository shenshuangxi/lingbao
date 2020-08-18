package com.sundy.lingbao.portal.controller.base;

import java.util.Objects;

import org.springframework.security.core.context.SecurityContextHolder;

import com.sundy.lingbao.portal.auth.AppUser;

public abstract class BaseController {

	public String getAccount() {
		if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof AppUser) {
				return ((AppUser) principal).getUsername();
			}
		}
		return null;
	}
	
	
}
