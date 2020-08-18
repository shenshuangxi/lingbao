package com.sundy.lingbao.portal;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sundy.lingbao.common.dto.AppDto;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;
import com.sundy.lingbao.portal.service.AppService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=LingbaoPortalApplication.class)
public class Test {

	@Autowired
	private AppService appService;
	
	@org.junit.Test
	public void crateApp() {
		try {
			AppEntity appEntity = new AppEntity();
			appEntity.setAppId("123");
			appEntity.setCreateBy("test");
			appEntity.setOrgName("testOrg");
			appEntity.setOwnerEmail("test@test.com");
			appEntity.setOwnerName("testOwner");
			appEntity.setUpdateBy("test");
			appService.createApp(BeanUtils.transfrom(AppDto.class, appEntity), "apollo");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
