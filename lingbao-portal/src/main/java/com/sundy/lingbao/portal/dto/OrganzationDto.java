package com.sundy.lingbao.portal.dto;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.dto.BaseDto;

@Getter
@Setter
public class OrganzationDto extends BaseDto {

	private String orgName;
	
	private Long parentId;
	
}
