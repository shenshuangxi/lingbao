package com.sundy.lingbao.biz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.biz.repository.ReleaseMessageRepository;

@Service
public class ReleaseMessageService {

	@Autowired
	private ReleaseMessageRepository releaseMessageRepository;

	public ReleaseMessageEntity findByMessage(String message) {
		ReleaseMessageEntity releaseMessageEntity = releaseMessageRepository.findFirstByMessageOrderByIdDesc(message);
		return releaseMessageEntity;
	}

	public ReleaseMessageEntity findById(Long releaseMessageId) {
		Optional<ReleaseMessageEntity> optional = releaseMessageRepository.findById(releaseMessageId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
}
