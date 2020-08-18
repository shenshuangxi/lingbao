package com.sundy.lingbao.portal.dto;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.dto.BaseDto;

@Getter
@Setter
public class EnvDto extends BaseDto{

	private String envId;
	
	private String name;
	
	private String url;
	
}
