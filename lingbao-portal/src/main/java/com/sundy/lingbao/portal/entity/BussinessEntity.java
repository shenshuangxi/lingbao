package com.sundy.lingbao.portal.entity;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;


@MappedSuperclass
@Getter
@Setter
public abstract class BussinessEntity extends BaseEntity {

	private String appId;
	
	private String clusterId;
	
	private String envId;
	
}
