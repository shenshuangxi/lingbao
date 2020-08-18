package com.sundy.lingbao.portal;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.sundy.lingbao.common.dto.ClusterDto;
import com.sundy.lingbao.portal.util.HttpClient;

public class ClusterTest {

	@Test
	public void createCluster() {
		ClusterDto clusterDto = new ClusterDto();
		clusterDto.setName("cluster-test-2");
		clusterDto.setParentClusterId("7c2ffcb45237449eaeade9c386f5bae1");
		ClusterDto newClusterDto = HttpClient.put("/api/v1/app/{appId}/env/{envId}/cluster", clusterDto, null, ClusterDto.class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174");
		System.out.println(JSON.toJSONString(newClusterDto, true));
	}
	
	@Test
	public void updateCluster() {
		ClusterDto clusterDto = new ClusterDto();
		clusterDto.setName("cluster-test");
		clusterDto.setInstanceIps("192.168.119.159");
		ClusterDto newClusterDto = HttpClient.patch("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}", clusterDto, null, ClusterDto.class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "7c2ffcb45237449eaeade9c386f5bae1");
		System.out.println(JSON.toJSONString(newClusterDto, true));
	}
	
	
	@Test
	public void deleteCluster() {
		ClusterDto newClusterDto = HttpClient.delete("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}", null, null, ClusterDto.class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "7c2ffcb45237449eaeade9c386f5bae1");
		System.out.println(JSON.toJSONString(newClusterDto, true));
	}
	

	@Test
	public void findAll() {
		ClusterDto[] newClusterDtos = HttpClient.get("/api/v1/app/{appId}/env/{envId}/cluster", null, null, ClusterDto[].class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174");
		System.out.println(JSON.toJSONString(newClusterDtos, true));
	}
	
	@Test 
	public void mergeCluster() {
		HttpClient.patch("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/merge", Boolean.FALSE, null, null, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "40ac8532768d480b9b347da905c8dcdb");
	}
	
	@Test
	public void commitFile() {
		HttpClient.patch("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file/commit", "test", null, null, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "7c2ffcb45237449eaeade9c386f5bae1");
	}
	
	@Test
	public void push() {
		HttpClient.post("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file/push", null, null, null, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "7c2ffcb45237449eaeade9c386f5bae1");
	}
	
	@Test
	public void rollBack() {
		HttpClient.patch("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file/commitKey/{commitKey}/rollback", "test", null, null, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "7c2ffcb45237449eaeade9c386f5bae1", "df8d6398416d49ceac74d3dcb6202012");
	}
	
}
