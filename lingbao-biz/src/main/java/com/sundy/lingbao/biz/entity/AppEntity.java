package com.sundy.lingbao.biz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import com.sundy.lingbao.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AppEntity extends BaseEntity {

	@Column(unique=true, nullable=false)
	private String appId;
	
	@Column(unique=true, nullable=false)
	private String name;
	
	private String orgName;
	
	private String ownerName;
	
	private String ownerEmail;
	
}
