package com.sundy.lingbao.portal.dto;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.dto.BaseDto;

@Getter
@Setter
public class PortalAppDto extends BaseDto {

	private String name;
	
	private Long email;
	
}
