package com.sundy.configservice.controller;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.sundy.configservice.wrapper.DeferredResultWrapper;
import com.sundy.lingbao.biz.entity.ClusterEntity;
import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.biz.message.ReleaseMessageListener;
import com.sundy.lingbao.biz.service.ClusterService;
import com.sundy.lingbao.biz.service.ReleaseMessageService;
import com.sundy.lingbao.biz.util.KeyUtil;
import com.sundy.lingbao.biz.util.EntityManagerUtil;
import com.sundy.lingbao.common.consts.Consts;
import com.sundy.lingbao.core.tracer.Tracer;


@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController implements ReleaseMessageListener {
	
	private final static Logger logger = LoggerFactory.getLogger(NotificationController.class);
	
	private final Multimap<String, DeferredResultWrapper> deferredResultMap = Multimaps.synchronizedSetMultimap(HashMultimap.create());
	
	@Autowired
	private ClusterService clusterService;
	
	@Autowired
	private ReleaseMessageService releaseMessageService;
	
	@Autowired
	private EntityManagerUtil entityManagerUtil;
	
	@Override
	public void handlerMessage(ReleaseMessageEntity releaseMessageEntity, String channel) {
		logger.info("handler release message [%s] notification [%d]  ", releaseMessageEntity.getMessage(), releaseMessageEntity.getId());
		if (Consts.Topic.RELEASE_MESSAGE.getName().equals(channel)) {
			if (deferredResultMap.containsKey(releaseMessageEntity.getMessage())){
				Collection<DeferredResultWrapper> deferredResults =  deferredResultMap.get(releaseMessageEntity.getMessage());
				if (Objects.nonNull(deferredResults)) {
					for (DeferredResultWrapper deferredResult : deferredResults) {
						deferredResult.setResult(releaseMessageEntity.getId());
					}
				}
			}
		}
	}

	@GetMapping
	public DeferredResult<ResponseEntity<Long>> pollNotification(
			@RequestParam(required=true) String appId, 
			@RequestParam(defaultValue="-1", required=false) Long releaseMessageId,
			@RequestParam(required=true) String clientIp) {
		
		ClusterEntity clusterEntity = clusterService.findByAppIdAndClientIp(appId, clientIp);
		
		String key = KeyUtil.assembleKey(appId, clusterEntity.getClusterId());
		ReleaseMessageEntity releaseMessageEntity = releaseMessageService.findByMessage(key);
		
		DeferredResultWrapper deferredResultWrapper = new DeferredResultWrapper(appId, clusterEntity.getName(), clientIp);
		if (releaseMessageEntity!=null && releaseMessageEntity.getId()>releaseMessageId) {
			deferredResultWrapper.setResult(releaseMessageEntity.getId());
		} else {
			deferredResultWrapper.onTimeout(()->{
				Tracer.logEvent("Lingbao.LongPoll.TimeoutKey", key);
			});
			deferredResultWrapper.onCompletion(() -> {
				//unregister all keys
				deferredResultMap.remove(key, deferredResultWrapper);
				Tracer.logEvent("Lingbao.LongPoll.TimeoutKey", key);
			});
			deferredResultMap.put(key, deferredResultWrapper);
		}
		return deferredResultWrapper.getDeferredResult();
	}
	
	
	
}
