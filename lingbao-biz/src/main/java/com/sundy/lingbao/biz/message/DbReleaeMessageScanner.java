package com.sundy.lingbao.biz.message;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.biz.repository.ReleaseMessageRepository;
import com.sundy.lingbao.common.consts.Consts;
import com.sundy.lingbao.core.util.LingbaoThreadFactory;

public class DbReleaeMessageScanner implements InitializingBean {

	private final static Logger logger = LoggerFactory.getLogger(DbReleaeMessageScanner.class);

	private List<ReleaseMessageListener> releaseMessageListeners;

	private final ScheduledExecutorService scheduledExecutorService;

	private long maxIdScanned = 0;

	@Autowired
	private ReleaseMessageRepository releaseMessageRepository;

	public DbReleaeMessageScanner() {
		releaseMessageListeners = Lists.newArrayList();
		scheduledExecutorService = Executors.newScheduledThreadPool(1, LingbaoThreadFactory.create("DbReleaeMessageScanner", true));
	}

	public void addMessageListener(ReleaseMessageListener listener) {
		if (!releaseMessageListeners.contains(listener)) {
			releaseMessageListeners.add(listener);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		maxIdScanned = loadLargestReleaseMessageId();
		scheduledExecutorService.scheduleWithFixedDelay(() -> {
			try {
				boolean hasMoreMessages = true;
				while (hasMoreMessages && !Thread.currentThread().isInterrupted()) {
					List<ReleaseMessageEntity> releaseMessageEntities = releaseMessageRepository.findFirst100ByIdGreaterThanOrderByIdAsc(maxIdScanned);
					if (CollectionUtils.isEmpty(releaseMessageEntities)) {
						hasMoreMessages = false;
					} else {
						fireMessageScanned(releaseMessageEntities);
						int messageScanned = releaseMessageEntities.size();
						maxIdScanned = releaseMessageEntities.get(messageScanned - 1).getId();
						hasMoreMessages = messageScanned == 500;
					}
				}
			} catch (Throwable e) {
				logger.error("operator scan releaseMessage error", e);
			}
		}, 10, 10, TimeUnit.SECONDS);
	}


	private long loadLargestReleaseMessageId() {
		ReleaseMessageEntity releaseMessageEntity = releaseMessageRepository.findTopByOrderByIdDesc();
		return releaseMessageEntity == null ? 0 : releaseMessageEntity.getId();
	}

	public void fireMessageScanned(List<ReleaseMessageEntity> releaseMessageEntities) {
		for (ReleaseMessageEntity releaseMessageEntity : releaseMessageEntities) {
			for (ReleaseMessageListener releaseMessageListener : releaseMessageListeners) {
				logger.info("Sending message {} to channel {} to {}", releaseMessageEntity.getMessage(), Consts.Topic.RELEASE_MESSAGE.getName(), releaseMessageListener.getClass().getSimpleName());
				releaseMessageListener.handlerMessage(releaseMessageEntity, Consts.Topic.RELEASE_MESSAGE.getName());
			}
		}
		
		
	}

}
