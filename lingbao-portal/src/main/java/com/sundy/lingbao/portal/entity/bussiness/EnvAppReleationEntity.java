package com.sundy.lingbao.portal.entity.bussiness;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnvAppReleationEntity extends BaseEntity {

	private String appId;
	
	private String envId;
	
}
