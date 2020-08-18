package com.sundy.lingbao.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserToken{

	private String token;
	
	private String nickName;
	
	private String name;
	
	private String phone;
	
	private String email;
	
}
