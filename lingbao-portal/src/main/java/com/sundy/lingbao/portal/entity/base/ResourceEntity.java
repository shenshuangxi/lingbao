package com.sundy.lingbao.portal.entity.base;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEntity extends BaseEntity {

	private Long userId;
	
	private String key;
	
	private String keyType;
	
	private Integer authType;
	
}
