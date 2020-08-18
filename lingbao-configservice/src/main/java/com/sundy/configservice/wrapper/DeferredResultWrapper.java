package com.sundy.configservice.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.sundy.lingbao.core.consts.GloablConst;

public class DeferredResultWrapper {

	private final DeferredResult<ResponseEntity<Long>> deferredResult;
	
	private final String appId;
	
	private final String cluster;
	
	private final String clientIp;

	public DeferredResultWrapper(String appId, String cluster, String clientIp) {
		this.appId = appId;
		this.cluster = cluster;
		this.clientIp = clientIp;
		this.deferredResult = new DeferredResult<>(GloablConst.LONG_POLLING_READ_TIMEOUT, new ResponseEntity<>(HttpStatus.NOT_MODIFIED));
	}

	public void onTimeout(Runnable timeoutCallback) {
		deferredResult.onTimeout(timeoutCallback);
	}

	public void onCompletion(Runnable completionCallback) {
		deferredResult.onCompletion(completionCallback);
	}

	public void setResult(Long releaseMessageId) {
		deferredResult.setResult(new ResponseEntity<Long>(releaseMessageId, HttpStatus.OK));
	}
	
	public DeferredResult<ResponseEntity<Long>> getDeferredResult() {
		return deferredResult;
	}

	public String getAppId() {
		return appId;
	}

	public String getCluster() {
		return cluster;
	}

	public String getClientIp() {
		return clientIp;
	}
	
	

}
