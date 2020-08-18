package com.sundy.lingbao.portal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sundy.lingbao.common.dto.ClusterDto;
import com.sundy.lingbao.common.dto.FileCommitTreeDto;
import com.sundy.lingbao.common.dto.FileSnapshotDto;
import com.sundy.lingbao.common.dto.FileTreeDto;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.exception.ServiceException;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.api.AdminServiceAPI;
import com.sundy.lingbao.portal.auth.AuthConst.AuthType;
import com.sundy.lingbao.portal.entity.base.AccountEntity;
import com.sundy.lingbao.portal.entity.base.ResourceEntity;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;
import com.sundy.lingbao.portal.entity.bussiness.ClusterEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvAppReleationEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileCommitTreeEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileSnapshotEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileTreeEntity;
import com.sundy.lingbao.portal.repository.base.AccountRepository;
import com.sundy.lingbao.portal.repository.base.EnvRepository;
import com.sundy.lingbao.portal.repository.base.ResourceRepository;
import com.sundy.lingbao.portal.repository.base.UserRepository;
import com.sundy.lingbao.portal.repository.bussiness.AppRepository;
import com.sundy.lingbao.portal.repository.bussiness.ClusterRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileCommitTreeRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileSnapshotRepository;
import com.sundy.lingbao.portal.repository.bussiness.FileTreeRepository;
import com.sundy.lingbao.portal.util.KeyUtil;

@Service
@Transactional
public class ClusterService {

	@Autowired
	private ClusterRepository clusterRepository;
	
	@Autowired
	private AdminServiceAPI.FileCommitTreeAPI fileCommitTreeAPI;
	
	@Autowired
	private AdminServiceAPI.ClusterAPI clusterAPI;
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCommitTreeRepository fileCommitTreeRepository;
	
	@Autowired
	private FileTreeRepository fileTreeRepository;
	
	@Autowired
	private FileSnapshotRepository fileSnapshotRepository;
	
	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AppRepository appRepository;
	
	@Autowired
	private EnvRepository envRepository;
	
	public List<ClusterEntity> findByAppIdAndEnvId(String appId, String envId, Sort sort) {
		List<ClusterEntity> clusterEntities = clusterRepository.findByAppIdAndEnvId(appId, envId, sort);
		return clusterEntities;
	}
	
	public ClusterEntity findByAppIdAndEnvIdMasterCluster(String appId, String envId, Sort sort) {
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndEnvIdAndParentClusterIdIsNull(appId, envId);
		return clusterEntity;
	}
	
	public List<ClusterEntity> findClusterChildren(String appId, String envId, String clusterId, Sort sort) {
		List<ClusterEntity> clusterEntities = clusterRepository.findByAppIdAndEnvIdAndParentClusterId(appId, envId, clusterId, sort);
		return clusterEntities;
	}
	
	public void createMasterCluster(String appId, String envId) {
		AppEntity appEntity = appRepository.findByAppId(appId);
		if (Objects.isNull(appEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "app not found");
		}
		EnvEntity envEntity = envRepository.findByEnvId(envId);
		if (Objects.isNull(envEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "env not found");
		}
		ClusterEntity masterClusterEntity = clusterRepository.findByAppIdAndEnvIdAndParentClusterIdIsNull(appId, envId);
		if (Objects.isNull(masterClusterEntity)) {
			ClusterEntity clusterEntity = new ClusterEntity();
			clusterEntity.setAppId(appId);
			clusterEntity.setEnvId(envId);
			clusterEntity.setClusterId(KeyUtil.getUUIDKey());
			clusterEntity.setName("Master");
			clusterEntity.setCreateBy(appEntity.getCreateBy());
			clusterEntity.setUpdateBy(appEntity.getCreateBy());
			clusterEntity = clusterRepository.save(clusterEntity);
			clusterAPI.create(envId, BeanUtils.transfrom(ClusterDto.class, clusterEntity));
			return;
		}
	}
	
	public ClusterEntity createCluster(String appId, String envId, ClusterDto clusterDto, String operatorAccount) {
		if (Objects.nonNull(clusterDto.getParentClusterId())) {
			ClusterEntity parentCluster = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterDto.getParentClusterId());
			if (Objects.isNull(parentCluster)) {
				throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "parent cluster not exist");
			}
			if (StringUtils.isNullOrEmpty(clusterDto.getInstanceIps())) {
				throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "child cluster ip must not empty");
			} else {
				String[] instanceIps = clusterDto.getInstanceIps().split(",");
				List<ClusterEntity> childClusters = clusterRepository.findByAppIdAndEnvIdAndParentClusterId(appId, envId, clusterDto.getParentClusterId());
				for (ClusterEntity clusterEntity : childClusters) {
					for (String instanceIp : instanceIps) {
						if (Arrays.asList(clusterEntity.getInstanceIps().split(",")).contains(instanceIp)) {
							throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("cluster [%s] exist ip [%s]", clusterEntity.getName(), instanceIp));
						}
					}
				}
			}
		} else {
			ClusterEntity clusterEntity = clusterRepository.findByAppIdAndEnvIdAndParentClusterIdIsNull(appId, envId);
			if (Objects.nonNull(clusterEntity)) {
				throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("appId [%s] env [%s] master cluster is exists", appId, envId));
			}
		}
		ClusterEntity clusterEntity = new ClusterEntity();
		clusterEntity.setAppId(appId);
		clusterEntity.setEnvId(envId);
		clusterEntity.setClusterId(KeyUtil.getUUIDKey());
		clusterEntity.setParentClusterId(clusterDto.getParentClusterId());
		clusterEntity.setInstanceIps(clusterDto.getInstanceIps());
		clusterEntity.setName(clusterDto.getName());
		clusterEntity.setCreateBy(operatorAccount);
		clusterEntity.setUpdateBy(operatorAccount);
		
		AccountEntity accountEntity = accountRepository.findByAccount(operatorAccount);
		resourceRepository.save(new ResourceEntity(accountEntity.getUserId(),  KeyUtil.getKey(appId, envId, clusterEntity.getClusterId()), ClusterEntity.class.getName(), AuthType.CREATE.getType()));
		resourceRepository.save(new ResourceEntity(accountEntity.getUserId(),  KeyUtil.getKey(appId, envId, clusterEntity.getClusterId()), ClusterEntity.class.getName(), AuthType.UPDATE.getType()));
		resourceRepository.save(new ResourceEntity(accountEntity.getUserId(),  KeyUtil.getKey(appId, envId, clusterEntity.getClusterId()), ClusterEntity.class.getName(), AuthType.DELETE.getType()));
		resourceRepository.save(new ResourceEntity(accountEntity.getUserId(),  KeyUtil.getKey(appId, envId, clusterEntity.getClusterId()), ClusterEntity.class.getName(), AuthType.COMMITPUSH.getType()));
		resourceRepository.save(new ResourceEntity(accountEntity.getUserId(),  KeyUtil.getKey(appId, envId, clusterEntity.getClusterId()), ClusterEntity.class.getName(), AuthType.FIND.getType()));
		
		clusterEntity = clusterRepository.save(clusterEntity);
		clusterAPI.create(clusterEntity.getEnvId(), BeanUtils.transfrom(ClusterDto.class, clusterEntity));
		return clusterEntity;
	}

	public ClusterEntity updateCluster(String appId, String envId, String clusterId, ClusterDto clusterDto, String operatorAccount) {
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
		if (Objects.nonNull(clusterEntity)) {
			if (clusterEntity.getInstanceIps()!=null && clusterEntity.getInstanceIps().equals(clusterDto.getInstanceIps())) {
				
			} else if (!StringUtils.isNullOrEmpty(clusterEntity.getParentClusterId())) {
				ClusterEntity parentCluster = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterEntity.getParentClusterId());
				if (Objects.isNull(parentCluster)) {
					throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "parent cluster not exist");
				}
				if (!StringUtils.isNullOrEmpty(parentCluster.getInstanceIps())) {
					if (StringUtils.isNullOrEmpty(clusterDto.getInstanceIps())) {
						throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "parent cluster ip not empty");
					}
					List<String> parentInstanceIps = Arrays.asList(parentCluster.getInstanceIps().split(","));
					for (String instanceIp : clusterDto.getInstanceIps().split(",")) {
						if (!parentInstanceIps.contains(instanceIp)) {
							throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("parent cluster not exist ip [%s]", instanceIp));
						}
					}
				}
			}
			clusterEntity.setInstanceIps(clusterDto.getInstanceIps());
			clusterEntity.setName(clusterDto.getName());
			clusterEntity.setUpdateBy(operatorAccount);
			clusterEntity.setUpdateDate(null);
			clusterEntity = clusterRepository.save(clusterEntity);
			clusterAPI.update(clusterEntity.getEnvId(), BeanUtils.transfrom(ClusterDto.class, clusterEntity));
			return clusterEntity;
		}
		throw new ServiceException("not found the cluster");
	}
	
	public void deleteCluster(String appId, String envId) {
		List<ClusterEntity> clusterEntities = clusterRepository.findByAppIdAndEnvId(appId, envId);
		if (Objects.nonNull(clusterEntities)) {
			List<ClusterEntity> parentClusterEntities = new ArrayList<ClusterEntity>();
			for (ClusterEntity clusterEntity : clusterEntities) {
				if (StringUtils.isNullOrEmpty(clusterEntity.getParentClusterId())) {
					parentClusterEntities.add(clusterEntity);
				} else {
					deleteCluster(appId, envId, clusterEntity.getClusterId());
				}
			}
			for (ClusterEntity clusterEntity : parentClusterEntities) {
				deleteCluster(appId, envId, clusterEntity.getClusterId());
			}
		}
	}

	public void deleteCluster(String appId, String envId, String clusterId) {
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
		if (Objects.nonNull(clusterEntity)) {
			List<ClusterEntity> childClusterEntities = clusterRepository.findByAppIdAndEnvIdAndParentClusterId(appId, envId, clusterEntity.getClusterId());
			if (Objects.nonNull(childClusterEntities) && childClusterEntities.size()>0) {
				for (ClusterEntity childClusterEntity : childClusterEntities) {
					deleteCluster(childClusterEntity.getAppId(), childClusterEntity.getEnvId(), childClusterEntity.getClusterId());
				}
			}
			
			//删除权限
			List<ResourceEntity> resourceEntities = resourceRepository.findByKeyStartingWith(KeyUtil.getKey(appId, envId, clusterId));
			if (Objects.nonNull(resourceEntities)) {
				resourceRepository.deleteAll(resourceEntities);
			}
			
			List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getEnvId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileCommitTreeEntities)) {
				fileCommitTreeRepository.deleteAll(fileCommitTreeEntities);
			}
			
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppIdAndEnvIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getEnvId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileTreeEntities)) {
				fileTreeRepository.deleteAll(fileTreeEntities);
			}
			
			List<FileSnapshotEntity> fileSnapshotEntities = fileSnapshotRepository.findByAppIdAndEnvIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getEnvId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileSnapshotEntities)) {
				fileSnapshotRepository.deleteAll(fileSnapshotEntities);
			}
			
			List<FileEntity> fileEntities = fileRepository.findByAppIdAndEnvIdAndClusterId(clusterEntity.getAppId(), clusterEntity.getEnvId(), clusterEntity.getClusterId());
			if (Objects.nonNull(fileEntities)) {
				fileRepository.deleteAll(fileEntities);
			}
			clusterRepository.delete(clusterEntity);
			clusterAPI.delete(envId, appId, clusterId);
			return;
		}
		throw new ServiceException("cluster not found");
	}
	
	

	public void merge(String appId, String envId, String clusterId, String operatorAccount) {
		ClusterEntity child = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
		if (Objects.isNull(child)) {
			throw new ServiceException(String.format("the child [%s] cluster not exist", child.getName()));
		}
		
		if (Objects.isNull(child.getParentClusterId())) {
			throw new ServiceException(String.format("the appId [%s]  cluster [%s] is the parent, can not merge", appId, child.getName()));
		}
		
		ClusterEntity parent = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, child.getParentClusterId());
		if (Objects.isNull(parent)) {
			throw new ServiceException(String.format("the parent id->[%s] cluster not exist", child.getParentClusterId()));
		}
		
		List<FileEntity> parentFileEntities = fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, parent.getClusterId());
		Map<String, FileEntity> parentFileEntityMap = parentFileEntities.stream().collect(Collectors.toMap(FileEntity::getName, FileEntity->FileEntity));
		List<FileEntity> childFileEntities = fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, child.getClusterId());
		
		if (Objects.nonNull(childFileEntities)) {
			for (FileEntity childFileEntity : childFileEntities) {
				if (Objects.nonNull(parentFileEntityMap) && parentFileEntityMap.containsKey(childFileEntity.getName())) {
					FileEntity parentFileEntity = parentFileEntityMap.get(childFileEntity.getName());
					Map<Integer, String> parentContents = parentFileEntity.getContents();
					Map<Integer, String> childContents = childFileEntity.getContents();
					if (Objects.nonNull(parentContents)) {
						Integer lineNum = 0;
						for (Integer parentLineNum : parentContents.keySet()) {
							if (parentLineNum>lineNum) {
								lineNum = parentLineNum;
							}
						}
						lineNum += 1;
						for (String value : childContents.values()) {
							parentContents.put(lineNum++, value);
						}
					} else {
						parentFileEntity.setContents(childContents);
					}
					parentFileEntity.setUpdateBy(operatorAccount);
					fileRepository.save(parentFileEntity);
				} else {
					FileEntity parentFileEntity = BeanUtils.transfrom(FileEntity.class, childFileEntity);
					parentFileEntity.setId(null);
					parentFileEntity.setClusterId(parent.getClusterId());
					parentFileEntity.setUpdateBy(operatorAccount);
					parentFileEntity.setUpdateDate(null);
					fileRepository.save(parentFileEntity);
				}
			}
		}
	}
	
	
	public void commitFile(String appId, String envId, String clusterId, String comment, String operator) {
		
		FileCommitTreeEntity prevFileCommitTreeEntity = fileCommitTreeRepository.findFirstByAppIdAndEnvIdAndClusterIdOrderByIdDesc(appId, envId, clusterId);
		List<FileTreeEntity> prevFileTreeEntities = new ArrayList<FileTreeEntity>();
		if (Objects.nonNull(prevFileCommitTreeEntity)) {
			prevFileTreeEntities = fileTreeRepository.findByAppIdAndEnvIdAndClusterIdAndFileTreeIdIn(appId, envId, clusterId, prevFileCommitTreeEntity.getChildrenFileTreeIds());
		}
		List<FileEntity> fileEntities = fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
		Map<String, FileEntity> fileKeyFileMap = fileEntities.stream().collect(Collectors.toMap(FileEntity::getName, fileEntity->fileEntity));
		
		List<FileSnapshotEntity> needStoreFileSnapshotEntities = new ArrayList<FileSnapshotEntity>();
		//存储所有不一致的 文件快照
		if (Objects.nonNull(fileEntities)) {
			for (FileEntity fileEntity : fileEntities) {
				FileSnapshotEntity fileSnapshotEntity = fileSnapshotRepository.findByAppIdAndEnvIdAndClusterIdAndFileKey(appId, envId, clusterId, fileEntity.getFileKey());
				if (Objects.isNull(fileSnapshotEntity)) {
					fileSnapshotEntity = new FileSnapshotEntity();
					fileSnapshotEntity.setAppId(appId);
					fileSnapshotEntity.setClusterId(clusterId);
					fileSnapshotEntity.setContents(fileEntity.getContents());
					fileSnapshotEntity.setEnvId(envId);
					fileSnapshotEntity.setFileKey(fileEntity.getFileKey());
					needStoreFileSnapshotEntities.add(fileSnapshotEntity);
				}
			}
		}
		
		String commitKey = KeyUtil.getUUIDKey();
		FileCommitTreeEntity newFileCommitTreeEntity = new FileCommitTreeEntity();
		newFileCommitTreeEntity.setAppId(appId);
		newFileCommitTreeEntity.setClusterId(clusterId);
		newFileCommitTreeEntity.setEnvId(envId);
		newFileCommitTreeEntity.setCommitKey(commitKey);
		newFileCommitTreeEntity.setComment(comment);
		newFileCommitTreeEntity.setCreateBy(operator);
		Set<String> childrenFileTreeIds = new HashSet<String>();
		if (Objects.nonNull(prevFileCommitTreeEntity)) {
			//第一步  获取一模一样的文件目录
			newFileCommitTreeEntity.setParentCommitKey(prevFileCommitTreeEntity.getCommitKey());
			for (FileTreeEntity fileTreeEntity : prevFileTreeEntities) {
				if(fileKeyFileMap.containsKey(fileTreeEntity.getTreeName()) && fileKeyFileMap.get(fileTreeEntity.getTreeName()).getFileKey().equals(fileTreeEntity.getFileKey())) {
					childrenFileTreeIds.add(fileTreeEntity.getFileTreeId());
					fileKeyFileMap.remove(fileTreeEntity.getTreeName());
				}
			}
		}
		
		List<FileTreeEntity> needStoreFileTreeEntities = new ArrayList<FileTreeEntity>();
		//存储不一致的文件目录
		for (FileEntity fileEntity : fileKeyFileMap.values()) {
			FileTreeEntity newFileTreeEntity = new FileTreeEntity();
			newFileTreeEntity.setAppId(appId);
			newFileTreeEntity.setClusterId(clusterId);
			newFileTreeEntity.setComment(comment);
			newFileTreeEntity.setCommitKey(commitKey);
			newFileTreeEntity.setCreateBy(operator);
			newFileTreeEntity.setEnvId(envId);
			newFileTreeEntity.setFileKey(fileEntity.getFileKey());
			newFileTreeEntity.setFileTreeId(KeyUtil.getUUIDKey());
			newFileTreeEntity.setTreeName(fileEntity.getName());
			newFileTreeEntity.setCreateBy(fileEntity.getCreateBy());
			newFileTreeEntity.setCreateDate(fileEntity.getCreateDate());
			newFileTreeEntity.setUpdateBy(fileEntity.getUpdateBy());
			newFileTreeEntity.setUpdateDate(fileEntity.getUpdateDate());
			needStoreFileTreeEntities.add(newFileTreeEntity);
			childrenFileTreeIds.add(newFileTreeEntity.getFileTreeId());
		}
		
		if (needStoreFileSnapshotEntities.isEmpty() && needStoreFileTreeEntities.isEmpty()) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "no file need commit");
		} else {
			if (!needStoreFileSnapshotEntities.isEmpty()) {
				fileSnapshotRepository.saveAll(needStoreFileSnapshotEntities);
			}
			if (!needStoreFileTreeEntities.isEmpty()) {
				fileTreeRepository.saveAll(needStoreFileTreeEntities);
			}
			
			newFileCommitTreeEntity.setChildrenFileTreeIds(childrenFileTreeIds);
			fileCommitTreeRepository.save(newFileCommitTreeEntity);
		}
	}
	
	
	public void rollbackFile(String appId, String envId, String clusterId, String commitKey, String operator) {
		//获取最近一次指定提交时的所有文件
		FileCommitTreeEntity fileCommitTreeEntity = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterIdAndCommitKey(appId, envId, clusterId, commitKey);
		List<FileEntity> fileEntities = new ArrayList<FileEntity>();
		if (Objects.nonNull(fileCommitTreeEntity) && Objects.nonNull(fileCommitTreeEntity.getChildrenFileTreeIds()) && fileCommitTreeEntity.getChildrenFileTreeIds().size()>0) {
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppIdAndEnvIdAndClusterIdAndFileTreeIdIn(appId, envId, clusterId, fileCommitTreeEntity.getChildrenFileTreeIds());
			if (Objects.nonNull(fileTreeEntities) && fileTreeEntities.size()>0) {
				for (FileTreeEntity fileTreeEntity : fileTreeEntities) {
					FileSnapshotEntity fileSnapshotEntity = fileSnapshotRepository.findByAppIdAndEnvIdAndClusterIdAndFileKey(appId, envId, clusterId, fileTreeEntity.getFileKey());
					FileEntity fileEntity = new FileEntity();
					fileEntity.setAppId(fileSnapshotEntity.getAppId());
					fileEntity.setClusterId(fileSnapshotEntity.getClusterId());
					fileEntity.setEnvId(fileSnapshotEntity.getEnvId());
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
		List<FileEntity> existsFileEntities = fileRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
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
		//删除所有比当前新的文件树
		List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterIdAndCreateDateGreaterThan(appId, envId, clusterId, fileCommitTreeEntity.getCreateDate());
		if (Objects.nonNull(fileCommitTreeEntities) && fileCommitTreeEntities.size()>0) {
			fileCommitTreeRepository.deleteAll(fileCommitTreeEntities);
		}
	}
	
	public void rollbackFile(String appId, String envId, String clusterId, String operator) {
		FileCommitTreeEntity fileCommitTreeEntity = fileCommitTreeRepository.findFirstByAppIdAndEnvIdAndClusterIdOrderByIdDesc(appId, envId, clusterId);
		if (Objects.nonNull(fileCommitTreeEntity)) {
			FileCommitTreeEntity parentFileCommitTreeEntity = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterIdAndCommitKey(appId, envId, clusterId, fileCommitTreeEntity.getParentCommitKey());
			if (Objects.nonNull(parentFileCommitTreeEntity)) {
				rollbackFile(appId, envId, clusterId, parentFileCommitTreeEntity.getCommitKey(), operator);
			} else {
				throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "this is the only one commit");
			}
		} else {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "no commit exists");
		}
	}
	
	
	public void pushFile(String appId, String envId, String clusterId) {
		ClusterEntity clusterEntity = clusterRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
		
		if (Objects.isNull(clusterEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "cluster is not exists");
		}
		
		FileCommitTreeEntity localNewfileCommitTreeEntity = fileCommitTreeRepository.findFirstByAppIdAndEnvIdAndClusterIdOrderByIdDesc(appId, envId, clusterId);
		if (Objects.isNull(localNewfileCommitTreeEntity)) {
			return;
		}
		//第一步  获取服务端的日志记录
		String remoteCommitKey = fileCommitTreeAPI.loadLatestCommitKey(envId, appId, clusterId);
		
		if (Objects.isNull(remoteCommitKey)) {
			List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
			List<FileCommitTreeDto> fileCommitTreeDtos = new ArrayList<FileCommitTreeDto>();
			for (FileCommitTreeEntity fileCommitTreeEntity : fileCommitTreeEntities) {
				fileCommitTreeDtos.add(getFileCommitTreeDto(fileCommitTreeEntity));
			}
			fileCommitTreeAPI.create(envId, appId, clusterId, fileCommitTreeDtos);
		} else {
			
			if (localNewfileCommitTreeEntity.getCommitKey().equals(remoteCommitKey)) {
				return;
			}
			
			if (localNewfileCommitTreeEntity.getParentCommitKey()!=null && localNewfileCommitTreeEntity.getParentCommitKey().equals(remoteCommitKey)) {
				fileCommitTreeAPI.create(envId, appId, clusterId, Collections.singleton(getFileCommitTreeDto(localNewfileCommitTreeEntity)));
				return;
			}
			FileCommitTreeEntity remoteFileCommitTreeEntity = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterIdAndCommitKey(appId, envId, clusterId, remoteCommitKey);
			if (Objects.nonNull(remoteFileCommitTreeEntity)) {
				//新建
				List<FileCommitTreeDto> fileCommitTreeDtos = new ArrayList<FileCommitTreeDto>();
				List<FileCommitTreeEntity> fileCommitTreeEntities = fileCommitTreeRepository.findByAppIdAndEnvIdAndClusterIdAndCreateDateGreaterThan(appId, envId, clusterId,  remoteFileCommitTreeEntity.getCreateDate());
				for (FileCommitTreeEntity fileCommitTreeEntity : fileCommitTreeEntities) {
					fileCommitTreeDtos.add(getFileCommitTreeDto(fileCommitTreeEntity));
				}
				fileCommitTreeAPI.create(envId, appId, clusterId, fileCommitTreeDtos);
			} else {
				//删除
				fileCommitTreeAPI.deleteNewThanCommitKey(envId, appId, clusterId, localNewfileCommitTreeEntity.getCommitKey());
			}
		}
	}
	
	public FileCommitTreeDto getFileCommitTreeDto(FileCommitTreeEntity fileCommitTreeEntity) {
		FileCommitTreeDto fileCommitTreeDto = new FileCommitTreeDto();
		fileCommitTreeDto.setAppId(fileCommitTreeEntity.getAppId());
		fileCommitTreeDto.setClusterId(fileCommitTreeEntity.getClusterId());
		fileCommitTreeDto.setCreateBy(fileCommitTreeEntity.getCreateBy());
		fileCommitTreeDto.setCreateDate(fileCommitTreeEntity.getCreateDate());
		fileCommitTreeDto.setParentCommitKey(fileCommitTreeEntity.getParentCommitKey());
		fileCommitTreeDto.setCommitKey(fileCommitTreeEntity.getCommitKey());
		fileCommitTreeDto.setComment(fileCommitTreeEntity.getComment());
		Collection<FileTreeDto> fileTreeDtos = getFileTreeDtos(fileCommitTreeEntity.getAppId(), fileCommitTreeEntity.getEnvId(), fileCommitTreeEntity.getClusterId(), fileCommitTreeEntity.getChildrenFileTreeIds());
		fileCommitTreeDto.setChildrenFileTreeDtos(fileTreeDtos);
		return fileCommitTreeDto;
	}



	private Collection<FileTreeDto> getFileTreeDtos(String appId, String envId, String clusterId, Collection<String> fileTreeIds) {
		List<FileTreeDto> fileTreeDtos = new ArrayList<FileTreeDto>();
		if (Objects.nonNull(fileTreeIds) && fileTreeIds.size()>0) {
			List<FileTreeEntity> fileTreeEntities = fileTreeRepository.findByAppIdAndEnvIdAndClusterIdAndFileTreeIdIn(appId, envId, clusterId, fileTreeIds);
			if (Objects.nonNull(fileTreeEntities)) {
				for (FileTreeEntity fileTreeEntity : fileTreeEntities) {
					FileTreeDto fileTreeDto = new FileTreeDto();
					fileTreeDto.setAppId(fileTreeEntity.getAppId());
					fileTreeDto.setClusterId(fileTreeEntity.getClusterId());
					fileTreeDto.setFileTreeId(fileTreeEntity.getFileTreeId());
					fileTreeDto.setCommitKey(fileTreeEntity.getCommitKey());
					fileTreeDto.setComment(fileTreeEntity.getComment());
					fileTreeDto.setTreeName(fileTreeEntity.getTreeName());
					fileTreeDto.setCreateBy(fileTreeEntity.getCreateBy());
					fileTreeDto.setCreateDate(fileTreeEntity.getCreateDate());
					fileTreeDto.setUpdateBy(fileTreeEntity.getUpdateBy());
					fileTreeDto.setUpdateDate(fileTreeEntity.getUpdateDate());
					FileSnapshotEntity fileSnapshotEntitiy = fileSnapshotRepository.findByAppIdAndEnvIdAndClusterIdAndFileKey(appId, envId, clusterId, fileTreeEntity.getFileKey());
					if (Objects.nonNull(fileSnapshotEntitiy)) {
						fileTreeDto.setFileSnapshotDto(BeanUtils.transfrom(FileSnapshotDto.class, fileSnapshotEntitiy));
					}
					fileTreeDtos.add(fileTreeDto);
				}
			}
		}
		return fileTreeDtos;
	}

	public void updateAppClusterPermission(String appId, String envId, Long userId, boolean isSetPermission, String account) {
		AccountEntity accountEntity = accountRepository.findByAccount(account);
		if (accountEntity.getUserId().equals(userId)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "can not modify self");
		}
		
		if (Objects.nonNull(userId)) {
			String key = KeyUtil.getKey(appId, envId);
			ResourceEntity resourceEntity = resourceRepository.findByUserIdAndKeyAndKeyTypeAndAuthType(userId, key, EnvAppReleationEntity.class.getName(), AuthType.FIND.getType());
			if (isSetPermission) {
				if (Objects.isNull(resourceEntity)) {
					resourceRepository.save(new ResourceEntity(userId, key, EnvAppReleationEntity.class.getName(), AuthType.FIND.getType()));
				}
			} else {
				if (Objects.nonNull(resourceEntity)) {
					resourceRepository.delete(resourceEntity);
				}
			}
		}
	}

}
