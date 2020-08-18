package com.sundy.lingbao.db.spring.config;

public class DbConfig {

	private String queryDataSourcePropertyPath;
	
	private String eventDataSourcePropertyPath;
	
	private String eventDir;
	
	private String invalidEventDir;
	
	private Integer fileEventCount;
	
	private Integer eventThreads;
	
	private String workerId;
	
	private String datacenterId;

	public String getQueryDataSourcePropertyPath() {
		return queryDataSourcePropertyPath;
	}

	public void setQueryDataSourcePropertyPath(String queryDataSourcePropertyPath) {
		this.queryDataSourcePropertyPath = queryDataSourcePropertyPath;
	}

	public String getEventDataSourcePropertyPath() {
		return eventDataSourcePropertyPath;
	}

	public void setEventDataSourcePropertyPath(String eventDataSourcePropertyPath) {
		this.eventDataSourcePropertyPath = eventDataSourcePropertyPath;
	}

	public String getEventDir() {
		return eventDir;
	}

	public void setEventDir(String eventDir) {
		this.eventDir = eventDir;
	}

	public String getInvalidEventDir() {
		return invalidEventDir;
	}

	public void setInvalidEventDir(String invalidEventDir) {
		this.invalidEventDir = invalidEventDir;
	}

	public Integer getFileEventCount() {
		return fileEventCount;
	}

	public void setFileEventCount(Integer fileEventCount) {
		this.fileEventCount = fileEventCount;
	}

	public Integer getEventThreads() {
		return eventThreads;
	}

	public void setEventThreads(Integer eventThreads) {
		this.eventThreads = eventThreads;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getDatacenterId() {
		return datacenterId;
	}

	public void setDatacenterId(String datacenterId) {
		this.datacenterId = datacenterId;
	}

	
	
	
}
