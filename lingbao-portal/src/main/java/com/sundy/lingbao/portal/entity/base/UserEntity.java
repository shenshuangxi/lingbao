package com.sundy.lingbao.portal.entity.base;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;

@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

	private String name;
	
	private String email;
	
	private String phone;
	
	private String address;
	
}
