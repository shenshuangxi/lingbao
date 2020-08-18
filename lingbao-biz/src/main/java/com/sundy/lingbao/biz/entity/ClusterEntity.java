package com.sundy.lingbao.biz.entity;


import javax.persistence.Column;
import javax.persistence.Entity;

import com.sundy.lingbao.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClusterEntity extends BaseEntity {

	private String appId;
	
	private String clusterId;
	
	private String parentClusterId;
	
	private String name;
	
	//includeIps 只能从其 父 集群中获取  如果没有父集群 则从 appId中获取  如何appId中没有  则可设置为null
	@Column(columnDefinition="text")
	private String instanceIps;
	
}
