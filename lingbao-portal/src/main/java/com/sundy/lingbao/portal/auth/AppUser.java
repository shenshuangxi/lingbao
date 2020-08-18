package com.sundy.lingbao.portal.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.entity.base.AccountEntity;

@SuppressWarnings("serial")
public class AppUser implements UserDetails {

	public static final String FAKE_PASSWORD = "p@ssw0rd";
	
	private final boolean needRefresh;
	
	private final AccountEntity accountEntity;
	
	public AppUser(AccountEntity accountEntity, boolean needRefresh) {
		this.accountEntity = accountEntity;
        this.needRefresh = needRefresh;
    }
	
	public Long getUserId() {
		return accountEntity.getUserId();
	}
	
	public AccountEntity getAccountEntity() {
		return accountEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (!StringUtils.isNullOrEmpty(accountEntity.getRoles())) {
            return Arrays.asList(accountEntity.getRoles().split(",")).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return FAKE_PASSWORD;
	}

	@Override
	public String getUsername() {
		return accountEntity.getAccount();
	}

	@Override
	public boolean isAccountNonExpired() {
		return !needRefresh;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
