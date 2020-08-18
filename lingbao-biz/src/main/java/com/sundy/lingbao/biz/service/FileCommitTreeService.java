package com.sundy.lingbao.biz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.sundy.lingbao.biz.entity.FileCommitTreeEntity;
import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.biz.entity.FileSnapshotEntity;
import com.sundy.lingbao.biz.entity.FileTreeEntity;
import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;
import com.sundy.lingbao.biz.message.ReleaseMessageSender;
import com.sundy.lingbao.biz.repository.FileCommitTreeRepository;
import com.sundy.lingbao.biz.repository.FileRepository;
import com.sundy.lingbao.biz.repository.FileSnapshotRepository;
import com.sundy.lingbao.biz.repository.FileTreeRepository;
import com.sundy.lingbao.biz.util.KeyUtil;
import com.sundy.lingbao.common.consts.Consts;
import com.sundy.lingbao.common.dto.FileCommitTreeDto;
import com.sundy.lingbao.common.dto.FileSnapshotDto;
import com.sundy.lingbao.common.dto.FileTreeDto;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.util.BeanUtils;


@Service
public class FileCommitTreeService {

	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCommitTreeRepository fileCommitTreeRepository;
	
	@Autowired
	private FileTreeRepository fileTreeRepository;
	
	@Autowired
	private FileSnapshotRepository fileSnapshotRepository;
	
	@Autowired
	private ReleaseMessageSender releaseMessageSender;

	public FileCommitTreeEntity findLatest(String appId, String clusterId) {
		FileCommitTreeEntity fileCommitTreeEntity = fileCommitTreeRepository.findFirstByAppIdAndClusterIdOrderByIdDesc(appId, clusterId);
		return fileCommitTreeEntity;
	}

	public List<FileCommitTreeEntity> findNewThanCommitKey(String appId, String clusterId, String commitKey) {
		FileCommitTreeEntity remotefileCommitTreeEntity = fileCommitTreeRepository.findByAppIdAndClusterIdAndCommitKey(appId, clusterId, commitKey);
		List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndClusterIdAndCreateDateGreaterThan(appId, clusterId, remotefileCommitTreeEntity.getCreateDate());
		return fileCommitTreeEntities;
	}
	
	@Transactional
	public void deleteNewThanCommitKey(String appId, String clusterId, String commitKey) {
		FileCommitTreeEntity remotefileCommitTreeEntity = fileCommitTreeRepository.findByAppIdAndClusterIdAndCommitKey(appId, clusterId, commitKey);
		
		List<FileEntity> fileEntities = new ArrayList<FileEntity>();
		if (Objects.nonNull(remotefileCommitTreeEntity) && Objects.nonNull(remotefileCommitTreeEntity.getChildrenFileTreeIds()) && remotefileCommitTreeEntity.getChildrenFileTreeIds().size()>0) {
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppIdAndClusterIdAndFileTreeIdIn(appId, clusterId, remotefileCommitTreeEntity.getChildrenFileTreeIds());
			if (Objects.nonNull(fileTreeEntities) && fileTreeEntities.size()>0) {
				for (FileTreeEntity fileTreeEntity : fileTreeEntities) {
					FileSnapshotEntity fileSnapshotEntity = fileSnapshotRepository.findByAppIdAndClusterIdAndFileKey(appId, clusterId, fileTreeEntity.getFileKey());
					FileEntity fileEntity = new FileEntity();
					fileEntity.setAppId(fileSnapshotEntity.getAppId());
					fileEntity.setClusterId(fileSnapshotEntity.getClusterId());
					fileEntity.setContents(fileSnapshotEntity.getContents());
					fileEntity.setFileKey(fileTreeEntity.getFileKey());
					fileEntity.setName(fileTreeEntity.getTreeName());
					fileEntity.setCreateBy(fileTreeEntity.getCreateBy());
					fileEntity.setCreateDate(fileTreeEntity.getCreateDate());
					fileEntity.setUpdateBy(fileTreeEntity.getUpdateBy());
					fileEntity.setUpdateDate(fileTreeEntity.getUpdateDate());
					fileEntities.add(fileEntity);
				}
			}
		} else {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "commitKey is not exists");
		}
		
		//清理当前工作区文件
		List<FileEntity> existsFileEntities = fileRepository.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.nonNull(existsFileEntities) && existsFileEntities.size()>0) {
			fileRepository.deleteAll(existsFileEntities);
		}
				
		//存储文件
		if (fileEntities.size()>0) {
			Collections.sort(fileEntities, new Comparator<FileEntity>() {
				@Override
				public int compare(FileEntity o1, FileEntity o2) {
					return (int) (o1.getCreateDate().getTime() - o2.getCreateDate().getTime());
				}
			});
			fileRepository.saveAll(fileEntities);
		}
		
		
		List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndClusterIdAndCreateDateGreaterThan(appId, clusterId, remotefileCommitTreeEntity.getCreateDate());
		if (Objects.nonNull(fileCommitTreeEntities)) {
			fileCommitTreeRepository.deleteAll(fileCommitTreeEntities);
		}
		releaseMessageSender.sendMessage(new ReleaseMessageEntity(appId, clusterId, commitKey, KeyUtil.assembleKey(appId, clusterId)), Consts.Topic.RELEASE_MESSAGE.getName());
	}

	@Transactional
	public void createFileCommitTreeEntities(String appId, String clusterId, ArrayList<FileCommitTreeDto> fileCommitTreeDtos) {
		if (Objects.nonNull(fileCommitTreeDtos)) {
			String latestCommitKey = null;
			Collections.sort(fileCommitTreeDtos, new Comparator<FileCommitTreeDto>() {
				@Override
				public int compare(FileCommitTreeDto o1, FileCommitTreeDto o2) {
					return (int) (o1.getCreateDate().getTime() - o2.getCreateDate().getTime());
				}
			});
			for (FileCommitTreeDto fileCommitTreeDto : fileCommitTreeDtos) {
				FileCommitTreeEntity fileCommitTreeEntity = new FileCommitTreeEntity();
				fileCommitTreeEntity.setAppId(fileCommitTreeDto.getAppId());
				fileCommitTreeEntity.setClusterId(fileCommitTreeDto.getClusterId());
				fileCommitTreeEntity.setComment(fileCommitTreeDto.getComment());
				fileCommitTreeEntity.setCommitKey(fileCommitTreeDto.getCommitKey());
				fileCommitTreeEntity.setCreateBy(fileCommitTreeDto.getCreateBy());
				fileCommitTreeEntity.setCreateDate(fileCommitTreeDto.getCreateDate());
				fileCommitTreeEntity.setParentCommitKey(fileCommitTreeDto.getParentCommitKey());
				latestCommitKey = fileCommitTreeEntity.getCommitKey();
				if (Objects.nonNull(fileCommitTreeDto.getChildrenFileTreeDtos()) && fileCommitTreeDto.getChildrenFileTreeDtos().size()>0) {
					List<String> childrenFileTreeIds = new ArrayList<String>();
					List<FileTreeDto> childrenFileTreeDtos = new ArrayList<FileTreeDto>(fileCommitTreeDto.getChildrenFileTreeDtos());
					Collections.sort(childrenFileTreeDtos, new Comparator<FileTreeDto>() {
						@Override
						public int compare(FileTreeDto o1, FileTreeDto o2) {
							return (int) (o1.getCreateDate().getTime() - o2.getCreateDate().getTime());
						}
					});
					for (FileTreeDto fileTreeDto : childrenFileTreeDtos) {
						childrenFileTreeIds.add(fileTreeDto.getFileTreeId());
						saveFileTreeEntity(fileTreeDto);
					}
					fileCommitTreeEntity.setChildrenFileTreeIds(Sets.newHashSet(childrenFileTreeIds));
				}
				fileCommitTreeRepository.save(fileCommitTreeEntity);
			}
			saveFile(appId, clusterId);
			releaseMessageSender.sendMessage(new ReleaseMessageEntity(appId, clusterId, latestCommitKey, KeyUtil.assembleKey(appId, clusterId)), Consts.Topic.RELEASE_MESSAGE.getName());
		}
	}
	
	
	private void saveFile(String appId, String clusterId) {
		List<FileEntity> existsFileEntities = fileRepository.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.nonNull(existsFileEntities) && existsFileEntities.size()>0) {
			fileRepository.deleteAll(existsFileEntities);
		}
		List<FileEntity> fileEntities = new ArrayList<FileEntity>();
		FileCommitTreeEntity fileCommitTreeEntity = fileCommitTreeRepository.findFirstByAppIdAndClusterIdOrderByIdDesc(appId, clusterId);
		if (Objects.nonNull(fileCommitTreeEntity) && Objects.nonNull(fileCommitTreeEntity.getChildrenFileTreeIds()) && fileCommitTreeEntity.getChildrenFileTreeIds().size()>0) {
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppIdAndClusterIdAndFileTreeIdIn(appId, clusterId, fileCommitTreeEntity.getChildrenFileTreeIds());
			if (Objects.nonNull(fileTreeEntities) && fileTreeEntities.size()>0) {
				for (FileTreeEntity fileTreeEntity : fileTreeEntities) {
					fileEntities.add(getFileEntity(fileTreeEntity));
				}
			}
		} 
		if (fileEntities.size()>0) {
			Collections.sort(fileEntities, new Comparator<FileEntity>() {
				@Override
				public int compare(FileEntity o1, FileEntity o2) {
					return (int) (o1.getCreateDate().getTime() - o2.getCreateDate().getTime());
				}
			});
			fileRepository.saveAll(fileEntities);
		}
	}
	
	private FileEntity getFileEntity(FileTreeEntity fileTreeEntity) {
		FileSnapshotEntity fileSnapshotEntity = fileSnapshotRepository.findByAppIdAndClusterIdAndFileKey(fileTreeEntity.getAppId(), fileTreeEntity.getClusterId(), fileTreeEntity.getFileKey());
		FileEntity fileEntity = new FileEntity();
		fileEntity.setAppId(fileSnapshotEntity.getAppId());
		fileEntity.setClusterId(fileSnapshotEntity.getClusterId());
		fileEntity.setContents(fileSnapshotEntity.getContents());
		fileEntity.setFileKey(fileSnapshotEntity.getFileKey());
		fileEntity.setName(fileTreeEntity.getTreeName());
		
		fileEntity.setCreateBy(fileTreeEntity.getCreateBy());
		fileEntity.setCreateDate(fileTreeEntity.getCreateDate());
		fileEntity.setUpdateBy(fileTreeEntity.getUpdateBy());
		fileEntity.setUpdateDate(fileTreeEntity.getUpdateDate());
		return fileEntity;
	}
	
	
	private void saveFileTreeEntity(FileTreeDto fileTreeDto) {
		FileTreeEntity fileTreeEntity = fileTreeRepository.findByAppIdAndClusterIdAndFileTreeId(fileTreeDto.getAppId(), fileTreeDto.getClusterId(), fileTreeDto.getFileTreeId());
		if (Objects.isNull(fileTreeEntity)) {
			fileTreeEntity = new FileTreeEntity();
			fileTreeEntity.setAppId(fileTreeDto.getAppId());
			fileTreeEntity.setClusterId(fileTreeDto.getClusterId());
			fileTreeEntity.setComment(fileTreeDto.getComment());
			fileTreeEntity.setCommitKey(fileTreeDto.getCommitKey());
			fileTreeEntity.setFileTreeId(fileTreeDto.getFileTreeId());
			fileTreeEntity.setTreeName(fileTreeDto.getTreeName());
			fileTreeEntity.setFileKey(fileTreeDto.getFileSnapshotDto().getFileKey());
			fileTreeEntity.setCreateBy(fileTreeDto.getCreateBy());
			fileTreeEntity.setCreateDate(fileTreeDto.getCreateDate());
			fileTreeEntity.setUpdateBy(fileTreeDto.getUpdateBy());
			fileTreeEntity.setUpdateDate(fileTreeDto.getUpdateDate());
			saveFileSnapshotEntity(fileTreeDto.getFileSnapshotDto());
			fileTreeRepository.save(fileTreeEntity);
		}
	}
	
	private void saveFileSnapshotEntity(FileSnapshotDto fileSnapshotDto) {
		FileSnapshotEntity fileSnapshotEntity = fileSnapshotRepository.findByAppIdAndClusterIdAndFileKey(fileSnapshotDto.getAppId(), fileSnapshotDto.getClusterId(), fileSnapshotDto.getFileKey());
		if (Objects.isNull(fileSnapshotEntity)) {
			fileSnapshotRepository.save(BeanUtils.transfrom(FileSnapshotEntity.class, fileSnapshotDto));
		}
	}

	
	
}
