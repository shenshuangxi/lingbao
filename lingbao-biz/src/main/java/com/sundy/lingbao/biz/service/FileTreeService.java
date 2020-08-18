package com.sundy.lingbao.biz.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.biz.entity.FileTreeEntity;
import com.sundy.lingbao.biz.repository.FileTreeRepository;

@Service
public class FileTreeService {

	@Autowired
	private FileTreeRepository fileTreeRepository;

	public List<FileTreeEntity> findByAppIdAndClusterIdAndFileTreeIdIn(String appId, String clusterId, Collection<String> fileTreeIds) {
		return fileTreeRepository.findByAppIdAndClusterIdAndFileTreeIdIn(appId, clusterId, fileTreeIds);
	}
	
}
