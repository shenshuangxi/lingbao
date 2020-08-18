package com.sundy.lingbao.portal.entity.bussiness;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.sundy.lingbao.portal.entity.BussinessEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClusterEntity extends BussinessEntity {

	private String parentClusterId;
	
	private String name;
	
	@Column(columnDefinition="text")
	private String instanceIps;
	
	
}
