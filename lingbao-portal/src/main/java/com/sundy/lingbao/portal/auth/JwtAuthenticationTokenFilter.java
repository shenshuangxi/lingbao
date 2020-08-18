package com.sundy.lingbao.portal.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sundy.lingbao.portal.component.config.PortalConfig;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
    private PortalConfig portalConfig;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authToken = request.getHeader(portalConfig.getJwtHeaderName());
		UserDetails userDetails = null;
        if (authToken != null) {
        	userDetails = jwtTokenUtil.getAppUserFromToken(authToken);
            if (userDetails != null) {
            	UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            	authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        try {
			filterChain.doFilter(request, response);
		} finally {
			if (userDetails != null && response.getStatus()==200 && !userDetails.isAccountNonExpired()) {
	            response.setStatus(203);
	        } else if (userDetails==null && SecurityContextHolder.getContext().getAuthentication() instanceof CasAuthenticationToken) {
	        	CasAuthenticationToken authentication = (CasAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
	        	AppUser appUser = (AppUser) authentication.getUserDetails();
	        	authToken = jwtTokenUtil.generateToken(appUser);
	        	response.addHeader(portalConfig.getJwtHeaderName(), authToken);
	        }
		}
	}

}
