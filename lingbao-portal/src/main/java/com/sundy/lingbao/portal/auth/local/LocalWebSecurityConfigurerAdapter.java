package com.sundy.lingbao.portal.auth.local;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.sundy.lingbao.common.condition.ConditionalOnProfile;
import com.sundy.lingbao.portal.auth.JwtAuthenticationTokenFilter;

@ConditionalOnProfile("localUserAuth")
@Configuration
public class LocalWebSecurityConfigurerAdapter  extends WebSecurityConfigurerAdapter {
	
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
		web.ignoring().antMatchers("/example/**");
		web.ignoring().antMatchers("/examples/**");
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors()
		.and()
        .csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests().antMatchers("/api/v1/auth/**").permitAll()
		.anyRequest().authenticated()
		.and()
		.addFilterBefore(jwtAuthenticationTokenFilter(), FilterSecurityInterceptor.class);
	}
	
	@Bean
	public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
		JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
		return jwtAuthenticationTokenFilter;
	}
	
}
