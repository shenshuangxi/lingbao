package com.sundy.lingbao.portal;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.sundy.lingbao.portal.entity.base.UserEntity;
import com.sundy.lingbao.portal.entity.bussiness.EnvEntity;
import com.sundy.lingbao.portal.util.KeyUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class JsonTest {

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	private static class User{
		private String name;
		private String price;
		private Integer id;
	}
	
	
	
	@SuppressWarnings("serial")
	public static void main(String[] args) {
		List<UserEntity> users = new ArrayList<UserEntity>() {
			{
				add(new UserEntity(){{
					setEmail("test@qq.com");
					setName("test");
					setPhone("123");
					setId(1l);
				}});
			}
		};
		System.out.println(JSON.toJSONString(users, true));
		
		List<EnvEntity> envs = new ArrayList<EnvEntity>() {
			{
				add(new EnvEntity(){{
					setUrl("test@qq.com1");
					setName("1234");
					setEnvId(KeyUtil.getUUIDKey());
				}});
				
				add(new EnvEntity(){{
					setUrl("test@qq.com2");
					setName("1235");
					setEnvId(KeyUtil.getUUIDKey());
				}});
				
				add(new EnvEntity(){{
					setUrl("test@qq.com3");
					setName("1236");
					setEnvId(KeyUtil.getUUIDKey());
				}});
			}
		};
		System.out.println(JSON.toJSONString(envs, true));
	}
	
}
