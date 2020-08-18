package com.sundy.db.store.event;


public class WrapEvent {

	private final DbEvent dbEvent;
	private final String successEventFileName;
	private final String fialEventFileName;
	
	public WrapEvent(DbEvent dbEvent, String successEventFileName,
			String fialEventFileName) {
		this.dbEvent = dbEvent;
		this.successEventFileName = successEventFileName;
		this.fialEventFileName = fialEventFileName;
	}
	
	public DbEvent getDbEvent() {
		return dbEvent;
	}
	public String getSuccessEventFileName() {
		return successEventFileName;
	}
	public String getFialEventFileName() {
		return fialEventFileName;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
