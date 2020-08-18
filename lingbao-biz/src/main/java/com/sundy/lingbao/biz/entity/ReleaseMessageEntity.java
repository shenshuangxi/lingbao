package com.sundy.lingbao.biz.entity;

import javax.persistence.Entity;

import com.sundy.lingbao.common.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseMessageEntity extends BaseEntity {

	private String appId;
	
	private String clusterId;
	
	private String commitKey;
	
	//appId+cluster
	private String message;
	
	
}
