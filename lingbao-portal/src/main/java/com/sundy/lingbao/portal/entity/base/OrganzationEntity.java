package com.sundy.lingbao.portal.entity.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"org_name", "parent_id"}))
@Getter
@Setter
public class OrganzationEntity extends BaseEntity {

	private String orgName;
	
	@Column(columnDefinition="bigint default 0")
	private Long parentId;
	
	private String hierarchy;
	
}
