package com.sundy.lingbao.portal.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;

import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.common.dto.ClusterDto;
import com.sundy.lingbao.common.dto.FileCommitTreeDto;
import com.sundy.lingbao.common.dto.InstanceConfigDto;
import com.sundy.lingbao.common.dto.InstanceDto;
import com.sundy.lingbao.portal.component.RetryableRestTemplate;

@Service
public class AdminServiceAPI {

	private static final String ADMIN_URL_PREFIX = "/api/v1";
	
	public static abstract class API {
		@Autowired
		protected RetryableRestTemplate retryableRestTemplate;
	}
	
	@Service
	public static class HealthAPI extends API {
		public Health health(String env) {
			return retryableRestTemplate.healthGet(env, "/health", null, Health.class);
	    }
	}
	
	@Service
	public static class AppAPI extends API {
		public void delete(String envId, String appId) {
			retryableRestTemplate.delete(envId, ADMIN_URL_PREFIX+"/app/{appId}", null, null, null, appId);
		}
		public void save(String envId, List<AppDto> appDtos) {
			retryableRestTemplate.post(envId, ADMIN_URL_PREFIX+"/app", appDtos, null, null);
		}
	}
	
	
	@Service
	public static class ClusterAPI extends API {
		public ClusterDto create(String envId, ClusterDto clusterDto) {
			return retryableRestTemplate.put(envId, ADMIN_URL_PREFIX+"/app/{appId}/cluster", clusterDto, null, ClusterDto.class, clusterDto.getAppId());
		}
		
		public ClusterDto update(String envId, ClusterDto clusterDto) {
			return retryableRestTemplate.patch(envId, ADMIN_URL_PREFIX+"/app/{appId}/cluster/{clusterId}", clusterDto, null, ClusterDto.class, clusterDto.getAppId(), clusterDto.getClusterId());
		}
		
		public void delete(String envId, String appId, String clusterId) {
			retryableRestTemplate.delete(envId, ADMIN_URL_PREFIX+"/app/{appId}/cluster/{clusterId}", null, null, null, appId, clusterId);
		}
	}
	
	@Service
	public static class FileCommitTreeAPI extends API {
		
		public String loadLatestCommitKey(String envId, String appId, String clusterId) {
			String commitKey = retryableRestTemplate.get(envId, ADMIN_URL_PREFIX+"/app/{appId}/cluster/{clusterId}/filecommittree/latest-commitkey", null, null, String.class, appId, clusterId);
			return commitKey;
		}
		
		public void create(String envId, String appId, String clusterId, Collection<FileCommitTreeDto> fileCommitTreeDtos) {
			retryableRestTemplate.put(envId, ADMIN_URL_PREFIX+"/app/{appId}/cluster/{clusterId}/filecommittree", fileCommitTreeDtos, null, null, appId, clusterId);
		}
		
		public void deleteNewThanCommitKey(String envId, String appId, String clusterId, String commitKey) {
			retryableRestTemplate.delete(envId, ADMIN_URL_PREFIX+"/app/{appId}/cluster/{clusterId}/filecommittree/newthan-commitkey/{commitKey}", null, null, null, appId, clusterId, commitKey);
		}

	}
	
	
	
	@Service
	public static class InstanceAPI extends API {
		public List<InstanceDto> loadAllInstance(String envId, String appId) {
			InstanceDto[] instanceDtos = retryableRestTemplate.get(envId, ADMIN_URL_PREFIX+"/app/{appid}/instance", null, null, InstanceDto[].class, appId);
			return Arrays.asList(instanceDtos);
		}
		public List<InstanceConfigDto> loadAllInstanceConfig(String envId, String appId, String cluster) {
			InstanceConfigDto[] instanceConfigDtos = retryableRestTemplate.get(envId, ADMIN_URL_PREFIX+"/app/{appid}/cluster/{cluster}/instance/{instanceId}/instanceconfig", null, null, InstanceConfigDto[].class, appId, cluster);
			return Arrays.asList(instanceConfigDtos);
		}
	}
	
}
