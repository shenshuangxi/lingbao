package com.sundy.lingbao.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstanceConfigDto extends BaseDto {

	private Long instanceId;
	
	private String appId;
	
	private String cluster;
	
	private String commitKey;
	
}
