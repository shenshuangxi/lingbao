package com.sundy.lingbao.portal.auth.cas;


import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.sundy.lingbao.common.condition.ConditionalOnProfile;
import com.sundy.lingbao.portal.auth.JwtAuthenticationTokenFilter;
import com.sundy.lingbao.portal.component.config.PortalConfig;

@ConditionalOnProfile("casAuth")
@Configuration
public class CasWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	
	
	@Autowired
	protected PortalConfig portalConfig;
	
	//配置全局静态资源免验证
	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.ignoring().antMatchers("/bootstrap/**");
		web.ignoring().antMatchers("/bootstrap-table/**");
		web.ignoring().antMatchers("/common/**");
		web.ignoring().antMatchers("/easyui/**");
		web.ignoring().antMatchers("/font-awesome/**");
		web.ignoring().antMatchers("/**/*.html");
		web.ignoring().antMatchers("/**/*.json");
		web.ignoring().antMatchers("/favicon.ico");
		web.ignoring().antMatchers("/bootstrap-select/**");
		web.ignoring().antMatchers("/select/**");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
		auth.authenticationProvider(casAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.headers().frameOptions().sameOrigin()
		.and()
		.authorizeRequests().antMatchers("/api/v1/auth/**").permitAll().anyRequest().authenticated()
		.and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint())
		.and()
		.addFilter(logoutFilter())
		.addFilter(casAuthenticationFilter())
		.addFilterBefore(jwtAuthenticationTokenFilter(), FilterSecurityInterceptor.class);
		
	}
	
	@Bean
	public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
		JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
		return jwtAuthenticationTokenFilter;
	}
	
	/**
	 * 用于判读登出跳转
	 * 第一步判断 request.getServletPath()+request.getPathInfo() 是否 符合 filterProcessUrl
	 * 第二步符合filterProcessUrl 通过SecurityContextLogoutHandler 清理掉 session 和 authentication 认证
	 * 第三步 通过SimpleUrlLogoutSuccessHandler进行caslogout url跳转
	 * @return
	 */
	@Bean
	public LogoutFilter logoutFilter() {
		LogoutFilter logoutFilter = new LogoutFilter(portalConfig.getCasLogoutUrl(), new SecurityContextLogoutHandler());
		logoutFilter.setFilterProcessesUrl(portalConfig.getLogoutPath());
		return logoutFilter;
	}
	
	/**
	 * CAS认证判断
	 * @return
	 */
	@Bean
	public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
		casAuthenticationEntryPoint.setLoginUrl(portalConfig.getCasLoginUrl());
		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
		return casAuthenticationEntryPoint;
	}
	
	
	
	/***************************************************用户认证********************************************************************************/
	
	/**
	 * 第一步 判断request是否需要认证 即默认设置的 (/login/cas) 或者 是 代理接收接口(用于做认证代理  即 设置了proxyReceptorUrl) 或者  处理代理认证票据(ticket  非/login/cas 且 serviceProperties中设置好 authenticateAllArtifacts为true 且该连接未认证)
	 * 第二步	 如需要认证  则进行认证  
	 * 			如果为代理认证  则直接返回 写response中验证成功  存入 pgtIou->pgtId 存入授权 库 用于在代理授权时  拿去cas进行验证
	 * 			如果为应用认证 则使用 _cas_stateful_(浏览器调用) 或  _cas_stateless_(client调用(非/login/cas)) 以及  ticket 对应的 值为 password  组成 UsernamePasswordAuthenticationToken
	 * 			调用 AuthenticationManager 回去 provider 进行认证
	 * 			provider 会从获取到的 ticket 去cas验证
	 * 		
	 * 第三步 
	 * @return
	 * @throws Exception 
	 */
	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		return casAuthenticationFilter;
	}
	
	@Bean
	public CasAuthenticationProvider casAuthenticationProvider() {
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider(); 
		casAuthenticationProvider.setAuthenticationUserDetailsService(lingbaoCasUserDetailsService());
		casAuthenticationProvider.setServiceProperties(serviceProperties());
		casAuthenticationProvider.setTicketValidator(ticketValidator());
		casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
		return casAuthenticationProvider;                                                   
	}
	
	@Bean
	public LingbaoCasUserDetailsService lingbaoCasUserDetailsService() {
		return new LingbaoCasUserDetailsService();
	}
	
	@Bean
	public ServiceProperties serviceProperties() {
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setSendRenew(portalConfig.getCasSendRenew());
		serviceProperties.setService(portalConfig.getBasepath()+portalConfig.getCasAuthentionPath());
		return serviceProperties;
	}
	
	@Bean
	public Cas30ServiceTicketValidator ticketValidator() {
		Cas30ServiceTicketValidator cas30ServiceTicketValidator = new Cas30ServiceTicketValidator(portalConfig.getCasServerUrl());
		return cas30ServiceTicketValidator;
	}
	
	
}
