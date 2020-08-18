package com.sundy.lingbao.biz.entity;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;


@Entity
@Getter
@Setter
public class InstanceEntity extends BaseEntity {

	private String appId;
	
	private String ip;
	
}
