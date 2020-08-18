package com.sundy.lingbao.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AccessPathCountHolder {

	private final static ThreadLocal<AccessPathCount> threadLocal = new ThreadLocal<AccessPathCount>(); 
	
	public static void set(AccessPathCount accessPathCount) {
		threadLocal.set(accessPathCount);
	}
	
	public static AccessPathCount get() {
		return threadLocal.get();
	}
	
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccessPathCount {
		
		private String path;
		
		private Long accessCount;
		
	}
	
}
