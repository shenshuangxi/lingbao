package com.sundy.lingbao.portal.entity.bussiness;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
public class ErrorEntity extends BaseEntity {

	@Enumerated(EnumType.STRING)
	private ErrorType type;
	
	private String key;
	
	private String data;
	
	private String message;
	
	
	public static enum ErrorType {
		App, Cluster, File; 
	}
	
}
