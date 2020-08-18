package com.sundy.lingbao.common.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sundy.lingbao.common.util.AccessPathCountHolder;
import com.sundy.lingbao.common.util.AccessPathCountHolder.AccessPathCount;

@Component
public class PVStaticFilter extends OncePerRequestFilter {

	public final static Long start = Calendar.getInstance().getTimeInMillis();
	
	public final static Map<String, Long> pvs = new ConcurrentHashMap<String, Long>();
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = request.getRequestURI();
		if (pvs.containsKey(path)){
			pvs.put(path, pvs.get(path)+1);
		} else {
			pvs.put(path, (long) 1);
		}
		AccessPathCountHolder.set(new AccessPathCount(path, pvs.get(path)));
		filterChain.doFilter(request, response);
	}


}
