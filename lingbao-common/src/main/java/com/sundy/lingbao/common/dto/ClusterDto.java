package com.sundy.lingbao.common.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClusterDto extends BussinessDto {

	private String parentClusterId;
	
	private String name;
	
	private String InstanceIps;
	
	
}
