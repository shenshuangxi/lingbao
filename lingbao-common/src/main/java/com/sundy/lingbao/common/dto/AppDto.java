package com.sundy.lingbao.common.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AppDto extends BaseDto {

	private Long userId;
	
	private String appId;
	
	private String name;
	
	private String orgName;
	
	private String ownerName;
	
	private String ownerEmail;
	
	private String ownerPhone;
	
	private String envIds;
	
	
}
