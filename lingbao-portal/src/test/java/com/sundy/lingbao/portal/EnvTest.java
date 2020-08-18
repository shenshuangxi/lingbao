package com.sundy.lingbao.portal;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSON;
import com.sundy.lingbao.portal.dto.EnvDto;
import com.sundy.lingbao.portal.dto.UserToken;
import com.sundy.lingbao.portal.util.HttpClient;

public class EnvTest {

	@Test
	public void create() {
		EnvDto envDto = new EnvDto();
		envDto.setName("Local");
		envDto.setUrl("http://localhost:8089/");
		EnvDto newEnvDto = HttpClient.put("/api/v1/env", envDto, null, EnvDto.class);
		System.out.println(JSON.toJSONString(newEnvDto, true));
		
	}
	
	@Test
	public void update() {
		EnvDto envDto = new EnvDto();
		envDto.setName("Local-Test");
		envDto.setEnvId("e4f53f189e1a4dc5911ac7f102fb1174");
		envDto.setUrl("http://localhost:8089/");
		EnvDto newEnvDto = HttpClient.patch("/api/v1/env/{envId}", envDto, null, EnvDto.class, envDto.getEnvId());
		System.out.println(JSON.toJSONString(newEnvDto, true));
	}
	
	@SuppressWarnings({ "serial"})
	@Test
	public void find() {
		try {
			
			MultiValueMap<String,Object> request = new LinkedMultiValueMap<String, Object>();
			request.add("account", "1695904589@qq.com");
			request.add("password", "888888");
			
			Map<String,String> header = new HashMap<String, String>();
			header.put("Content-Type", "application/x-www-form-urlencoded");
			
			UserToken userToken = HttpClient.post("/api/v1/auth/login", request, header, UserToken.class);
			
			EnvDto[] envDtos = HttpClient.get("/api/v1/env", null, new HashMap<String, String>(){{
				put("Authentication", userToken.getToken());
			}}, EnvDto[].class);
			System.out.println(JSON.toJSONString(envDtos, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		
	}
	
}
