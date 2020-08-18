package com.sundy.lingbao.core.consts;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public interface GlobalGsonTypes {

	Type mapType = new TypeToken<Map<Object, Object>>() {}.getType();
	
	Type setType = new TypeToken<Set<Object>>() {}.getType();
	
}
