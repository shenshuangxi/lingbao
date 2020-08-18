package com.sundy.db.store.event;

import java.io.Serializable;

public class EventIndex implements Serializable {

	private static final long serialVersionUID = -1399852326313897702L;
	
	private final long startIndex;
	private final long endIndex;
	private final String eventFileName;
	
	public EventIndex(long startIndex, long endIndex, String eventFileName) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.eventFileName = eventFileName;
	}
	
	public long getStartIndex() {
		return startIndex;
	}

	public long getEndIndex() {
		return endIndex;
	}

	public String getEventFileName() {
		return eventFileName;
	}
	
	
	
	
	
	
}
