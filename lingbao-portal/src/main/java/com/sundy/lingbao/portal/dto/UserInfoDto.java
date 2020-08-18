package com.sundy.lingbao.portal.dto;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.dto.BaseDto;

@Getter
@Setter
public class UserInfoDto extends BaseDto {

	private Long userId;
	
	private String name;
	
	private String email;
	
	private String phone;
	
}
