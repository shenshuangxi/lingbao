package com.sundy.lingbao.portal.util;

import org.springframework.util.Assert;

import com.sundy.lingbao.portal.util.KeyUtil;

public class EventKeyHolder {

	private static final ThreadLocal<String> eventKeyHolder = new ThreadLocal<>();
	
	public static void clearEventKey() {
		eventKeyHolder.remove();
	}

	public static String getEventKey() {
		String eventKey = eventKeyHolder.get();
		if (eventKey == null) {
			eventKey = createEventKey();
			eventKeyHolder.set(eventKey);
		}
		return eventKey;
	}

	public static void setEventKey(String eventKey) {
		Assert.notNull(eventKey, "Only non-null event key instances are permitted");
		eventKeyHolder.set(eventKey);
	}

	public static String createEventKey() {
		return KeyUtil.getUUIDKey();
	}
	
}
