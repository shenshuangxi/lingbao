package com.sundy.lingbao.portal.auth.cas;

import java.util.Objects;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sundy.lingbao.portal.auth.AppUser;
import com.sundy.lingbao.portal.entity.base.AccountEntity;
import com.sundy.lingbao.portal.repository.base.AccountRepository;

public class LingbaoCasUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

	private final static Logger logger = LoggerFactory.getLogger(LingbaoAccessDessionManager.class);
	
	public static final String FAKE_PASSWORD = "p@ssw0rd";
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		logger.info("-------------------------loadUserDetails----------------------------");
		AttributePrincipal principal = token.getAssertion().getPrincipal();
		if (Objects.nonNull(principal) && Objects.nonNull(principal.getAttributes().get("globelId"))) {
			Long globalId = Long.parseLong(principal.getAttributes().get("globelId").toString());
			
			AccountEntity accountEntity = accountRepository.findByGlobalId(globalId);
			if (Objects.isNull(accountEntity)) {
				throw new UsernameNotFoundException("Wrong username or password.");
			}
			AppUser appUser = new AppUser(accountEntity, false);
			return appUser;
		}
		throw new UsernameNotFoundException("user not found!");
	}


}
