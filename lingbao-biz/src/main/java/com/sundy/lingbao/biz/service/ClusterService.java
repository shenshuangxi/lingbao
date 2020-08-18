package com.sundy.lingbao.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sundy.lingbao.biz.entity.AppEntity;
import com.sundy.lingbao.biz.entity.ClusterEntity;
import com.sundy.lingbao.biz.entity.FileCommitTreeEntity;
import com.sundy.lingbao.biz.entity.FileEntity;
import com.sundy.lingbao.biz.entity.FileSnapshotEntity;
import com.sundy.lingbao.biz.entity.FileTreeEntity;
import com.sundy.lingbao.biz.repository.AppRepository;
import com.sundy.lingbao.biz.repository.ClusterRepository;
import com.sundy.lingbao.biz.repository.FileCommitTreeRepository;
import com.sundy.lingbao.biz.repository.FileRepository;
import com.sundy.lingbao.biz.repository.FileSnapshotRepository;
import com.sundy.lingbao.biz.repository.FileTreeRepository;
import com.sundy.lingbao.common.dto.ClusterDto;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.exception.ServiceException;
import com.sundy.lingbao.core.util.StringUtils;

@Service
@Transactional
public class ClusterService {

	@Autowired
	private AppRepository appRepository;
	
	@Autowired
	private ClusterRepository clusterRepository;
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCommitTreeRepository fileCommitTreeRepository;
	
	@Autowired
	private FileTreeRepository fileTreeRepository;
	
	@Autowired
	private FileSnapshotRepository fileSnapshotRepository;
	
	public ClusterEntity findByAppIdAndClientIp(String appId, String clientIp) {
		List<ClusterEntity> clusterEntities = clusterRepository.findByAppId(appId);
		if (Objects.nonNull(clusterEntities)) {
			ClusterEntity realCluster = null;
			for (ClusterEntity clusterEntity : clusterEntities) {
				if (Objects.nonNull(clusterEntity.getInstanceIps())) {
					if (clusterEntity.getInstanceIps().contains(clientIp)) {
						if (!StringUtils.isNullOrEmpty(clusterEntity.getParentClusterId())) {
							return clusterEntity;
						} else {
							realCluster = clusterEntity;
						}
					}
				} else {
					realCluster = clusterEntity;
				}
			}
			return realCluster;
		}
		return null;
	}
	
	public List<ClusterEntity> findByAppId(String appId) {
		return clusterRepository.findByAppId(appId);
	}
	
	public List<ClusterEntity> findByAppId(String appId, Sort sort) {
		List<ClusterEntity> clusterEntities = clusterRepository.findByAppId(appId, sort);
		return clusterEntities;
	}
	
	public ClusterEntity createCluster(String appId, ClusterDto clusterDto) {
		if (Objects.nonNull(clusterDto.getParentClusterId())) {
			ClusterEntity parentCluster = clusterRepository.findByAppIdAndClusterId(appId, clusterDto.getParentClusterId());
			if (Objects.isNull(parentCluster)) {
				throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "parent cluster not exist");
			}
		}
		AppEntity appEntity = appRepository.findByAppId(appId);
		if (Objects.isNull(appEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "app not found");
		}
		ClusterEntity clusterEntity = new ClusterEntity();
		clusterEntity.setAppId(clusterDto.getAppId());
		clusterEntity.setClusterId(clusterDto.getClusterId());
		clusterEntity.setInstanceIps(clusterDto.getInstanceIps());
		clusterEntity.setName(clusterDto.getName());
		clusterEntity.setParentClusterId(clusterDto.getParentClusterId());
		clusterEntity.setCreateBy(clusterDto.getCreateBy());
		clusterEntity.setCreateDate(clusterDto.getCreateDate());
		clusterEntity.setUpdateBy(clusterDto.getUpdateBy());
		clusterEntity.setUpdateDate(clusterDto.getUpdateDate());
		return clusterRepository.save(clusterEntity);
	}

	public ClusterEntity updateCluster(String appId, String clusterId, ClusterDto clusterDto) {
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.nonNull(clusterEntity)) {
			clusterEntity.setInstanceIps(clusterDto.getInstanceIps());
			clusterEntity.setName(clusterDto.getName());
			clusterEntity.setUpdateBy(clusterDto.getUpdateBy());
			clusterEntity.setUpdateDate(clusterDto.getUpdateDate());
			return clusterRepository.save(clusterEntity);
		}
		throw new ServiceException("not found the cluster");
	}
	
	public void deleteCluster(String appId) {
		List<ClusterEntity> clusterEntities = clusterRepository.findByAppId(appId);
		if (Objects.nonNull(clusterEntities)) {
			List<ClusterEntity> parentClusterEntities = new ArrayList<ClusterEntity>();
			for (ClusterEntity clusterEntity : clusterEntities) {
				if (StringUtils.isNullOrEmpty(clusterEntity.getParentClusterId())) {
					parentClusterEntities.add(clusterEntity);
				} else {
					deleteCluster(appId, clusterEntity.getClusterId());
				}
			}
			for (ClusterEntity clusterEntity : parentClusterEntities) {
				deleteCluster(appId, clusterEntity.getClusterId());
			}
		}
	}

	public void deleteCluster(String appId, String clusterId) {
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.nonNull(clusterEntity)) {
			List<ClusterEntity> childClusterEntities = clusterRepository.findByAppIdAndParentClusterId(appId, clusterEntity.getClusterId());
			if (Objects.nonNull(childClusterEntities) && childClusterEntities.size()>0) {
				for (ClusterEntity childClusterEntity : childClusterEntities) {
					deleteCluster(childClusterEntity.getAppId(), childClusterEntity.getClusterId());
				}
			}
			
			List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileCommitTreeEntities)) {
				fileCommitTreeRepository.deleteAll(fileCommitTreeEntities);
			}
			
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileTreeEntities)) {
				fileTreeRepository.deleteAll(fileTreeEntities);
			}
			
			List<FileSnapshotEntity> fileSnapshotEntities = fileSnapshotRepository.findByAppIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileSnapshotEntities)) {
				fileSnapshotRepository.deleteAll(fileSnapshotEntities);
			}
			
			List<FileEntity> fileEntities = fileRepository.findByAppIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileEntities)) {
				fileRepository.deleteAll(fileEntities);
			}
			clusterRepository.delete(clusterEntity);
		}
	}

	public void merge(String appId, String clusterId, String operator, boolean deleteBranch) {
		ClusterEntity child = clusterRepository.findByAppIdAndClusterId(appId, clusterId);
		if (Objects.isNull(child)) {
			throw new ServiceException(String.format("the child [%s] cluster not exist", child.getName()));
		}
		
		if (Objects.nonNull(child.getParentClusterId())) {
			throw new ServiceException(String.format("the appId {}  cluster {} is the parent, can not merge", appId, child.getName()));
		}
		
		ClusterEntity parent = clusterRepository.findByAppIdAndClusterId(appId, child.getParentClusterId());
		if (Objects.isNull(parent)) {
			throw new ServiceException(String.format("the parent id->{} cluster not exist", child.getParentClusterId()));
		}
		
		List<FileEntity> parentFileEntities = fileRepository.findByAppIdAndClusterId(appId, parent.getClusterId());
		Map<String, FileEntity> parentFileEntityMap = parentFileEntities.stream().collect(Collectors.toMap(FileEntity::getName, FileEntity->FileEntity));
		List<FileEntity> childFileEntities = fileRepository.findByAppIdAndClusterId(appId, child.getClusterId());
		
		if (Objects.nonNull(childFileEntities)) {
			for (FileEntity childFileEntity : childFileEntities) {
				if (Objects.nonNull(parentFileEntityMap) && parentFileEntityMap.containsKey(childFileEntity.getName())) {
					FileEntity parentFileEntity = parentFileEntityMap.get(childFileEntity.getName());
					Map<Integer, String> parentContents = parentFileEntity.getContents();
					Map<Integer, String> childContents = childFileEntity.getContents();
					Integer lineNum = 1;
					for (Integer parentLineNum : parentContents.keySet()) {
						if (parentLineNum>lineNum) {
							lineNum = parentLineNum;
						}
					}
					lineNum += 1;
					for (String value : childContents.values()) {
						parentContents.put(lineNum++, value);
					}
					parentFileEntity.setUpdateBy(operator);
					fileRepository.save(parentFileEntity);
				} else {
					childFileEntity.setId(null);
					childFileEntity.setUpdateBy(operator);
					fileRepository.save(childFileEntity);
				}
			}
		}
		
		if (deleteBranch) {
			deleteCluster(child.getAppId(), child.getClusterId());
		} 
		
	}
	
	
	
	
}
