package com.sundy.lingbao.portal;

import java.util.HashMap;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.sundy.lingbao.common.dto.FileDto;
import com.sundy.lingbao.portal.util.HttpClient;

public class FileTest {

	@Test
	public void create() {
		FileDto fileDto = new FileDto();
		fileDto.setName("test6.properties");
		FileDto dto = HttpClient.put("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file", fileDto, null, FileDto.class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "40ac8532768d480b9b347da905c8dcdb");
		System.out.println(JSON.toJSONString(dto, true));
	}
	
	@SuppressWarnings("serial")
	@Test
	public void update() {
		FileDto fileDto = new FileDto();
		fileDto.setName("test3.properties");
		fileDto.setContents(new HashMap<Integer, String>(){{
			put(1, "test");
			put(2, "test3");
			put(3, "test4");
		}});
		FileDto newfileDto = HttpClient.patch("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file/{id}", fileDto, null, FileDto.class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "40ac8532768d480b9b347da905c8dcdb", 19);
		System.out.println(JSON.toJSONString(newfileDto, true));
	}
	
	@Test
	public void delete() {
		HttpClient.patch("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file/{fileKey}", null, null, null, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "7c2ffcb45237449eaeade9c386f5bae1", "99E0D5F3CFE7E189007EB4D8308A2D1C");
	}
	
	@Test
	public void find() {
		FileDto[] fileDtos = HttpClient.get("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file", null, null, FileDto[].class, "123457890", "e4f53f189e1a4dc5911ac7f102fb1174", "99E0D5F3CFE7E189007EB4D8308A2D1C");
		System.out.println(JSON.toJSONString(fileDtos, true));
	}
	
}
