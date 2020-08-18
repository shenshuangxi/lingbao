package com.sundy.lingbao.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.biz.entity.FileSnapshotEntity;
import com.sundy.lingbao.biz.repository.FileSnapshotRepository;

@Service
public class FileSnapshotService {

	@Autowired
	private FileSnapshotRepository fileSnapshotRepository;

	public FileSnapshotEntity findByAppIdAndClusterIdAndFileKey(String appId, String clusterId, String fileKey) {
		return fileSnapshotRepository.findByAppIdAndClusterIdAndFileKey(appId, clusterId, fileKey);
	}
	
}
