package com.sundy.lingbao.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BussinessDto extends BaseDto {

	private String appId;
	
	private String clusterId;
	
	private String envId;
	
}
