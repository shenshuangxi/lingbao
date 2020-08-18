package com.sundy.lingbao.portal.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResourceDto {
	
	private Long userId;
	
	private String key;
	
	private String keyType;
	
	private Integer authType;
}
