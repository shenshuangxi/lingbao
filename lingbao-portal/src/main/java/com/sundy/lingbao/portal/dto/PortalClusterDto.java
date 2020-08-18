package com.sundy.lingbao.portal.dto;

import java.util.Set;

import com.sundy.lingbao.common.dto.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortalClusterDto extends BaseDto {

	private String appId;
	
	private String name;
	
	private Long parentId = 0l;
	
	private Set<String> includeIps;
	
}
