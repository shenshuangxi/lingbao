package com.sundy.lingbao.portal.entity.base;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;

@Getter
@Setter
@Entity
public class AccountEntity extends BaseEntity {

	private Long globalId;
	
	private String account;
	
	private String password;
	
	private String unionId;
	
	private String openId;
	
	private String nickName;
	
	private String roles;
	
	private Long userId;
	
}
