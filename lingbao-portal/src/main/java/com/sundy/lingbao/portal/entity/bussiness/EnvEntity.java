package com.sundy.lingbao.portal.entity.bussiness;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.util.KeyUtil;

@Entity
@Getter
@Setter
public class EnvEntity extends BaseEntity {

	private String envId;
	private String name;
	private String url;
	
	private Integer failConnectionCount;
	
	
	@PrePersist
	public void preEnvPersist() {
		if (StringUtils.isNullOrEmpty(envId)) {
			envId = KeyUtil.getUUIDKey();
		}
	}
	
}
