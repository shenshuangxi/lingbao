package com.sundy.lingbao.biz.message;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Queues;
import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.biz.repository.ReleaseMessageRepository;
import com.sundy.lingbao.common.consts.Consts;
import com.sundy.lingbao.core.util.LingbaoThreadFactory;


@Component
public class DbReleaseMessageSender implements ReleaseMessageSender {

	private final static Logger logger = LoggerFactory.getLogger(DbReleaseMessageSender.class);

	private final BlockingQueue<Long> toCleanQueue = Queues.newLinkedBlockingQueue();
	private final ExecutorService toCleanExecutorService;


	@Autowired
	private ReleaseMessageRepository releaseMessageRepository;

	public DbReleaseMessageSender() {
		toCleanExecutorService = Executors.newSingleThreadExecutor(LingbaoThreadFactory.create("DbReleaseMessageSender", true));
	}

	@PostConstruct
	public void initialize() {
		toCleanExecutorService.submit(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Long releaseMessageId = toCleanQueue.poll(5, TimeUnit.SECONDS);
					if (releaseMessageId != null) {
						cleanReleaseMessage(releaseMessageId);
					} else {
						TimeUnit.SECONDS.sleep(10);
					}
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	private void cleanReleaseMessage(Long releaseMessageId) {
		Optional<ReleaseMessageEntity> optional = releaseMessageRepository.findById(releaseMessageId);
		if (optional.isPresent()) {
			boolean hasMore = true;
			while (hasMore && !Thread.currentThread().isInterrupted()) {
				List<ReleaseMessageEntity> releaseMessageEntities = releaseMessageRepository.findFirst100ByMessageAndIdLessThanOrderByIdAsc(optional.get().getMessage(), optional.get().getId());
				if (Objects.nonNull(releaseMessageEntities)) {
					releaseMessageRepository.deleteAll(releaseMessageEntities);
					hasMore = releaseMessageEntities.size() == 100;
				} else {
					hasMore = false;
				}
			}
		}

	}

	@Override
	public void sendMessage(ReleaseMessageEntity releaseMessageEntity, String channel) {
		logger.info("Sending message {} to channel {}", releaseMessageEntity.getMessage(), channel);
		if (!Objects.equals(channel, Consts.Topic.RELEASE_MESSAGE.getName())) {
			logger.warn("Channel {} not supported by DbReleaseMessageSender!");
			return;
		}
		ReleaseMessageEntity messageEntity = releaseMessageRepository.save(releaseMessageEntity);
		toCleanQueue.offer(messageEntity.getId());
	}

}
