package com.sundy.lingbao.portal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.portal.util.HttpClient;

public class AppTest {

	
	@Test
	public void find() {
		AppDto[] appDtos = HttpClient.get("/api/v1/app", null, null, AppDto[].class);
		System.out.println(JSON.toJSONString(appDtos, true));
	}
	
	@Test
	public void create() {
		AppDto appDto = new AppDto();
		appDto.setAppId("123457890");
		appDto.setName("test");
		appDto.setOrgName("test");
		appDto.setOwnerEmail("test@lingbao.com");
		appDto.setOwnerName("test");
		AppDto newAppDto = HttpClient.put("/api/v1/app", appDto, null, AppDto.class);
		System.out.println(JSON.toJSONString(newAppDto, true));
	}
	
	@Test
	public void update() {
		AppDto appDto = new AppDto();
		appDto.setName("test-1");
		appDto.setOrgName("test");
		appDto.setOwnerEmail("test@lingbao.com");
		appDto.setOwnerName("test");
		AppDto newAppDto = HttpClient.patch("/api/v1/app/{appId}", appDto, null, AppDto.class, "123457890");
		System.out.println(JSON.toJSONString(newAppDto, true));
	}
	
	@Test
	public void appReleationEnvs() {
		List<String> envIds = new ArrayList<String>();
		envIds.add("e4f53f189e1a4dc5911ac7f102fb1174");
		String ret = HttpClient.patch("/api/v1/app//{appId}/app-relation-envs", envIds, null, String.class, "123457890");
		System.out.println(ret);
	}
	
	
	@Test
	public void commit() {
		String ret = HttpClient.post("/api/v1/app/{appId}/commit", null, null, String.class, "123457890");
		System.out.println(ret);
	}
	
}
