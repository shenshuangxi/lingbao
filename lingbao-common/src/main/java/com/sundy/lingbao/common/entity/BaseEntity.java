package com.sundy.lingbao.common.entity;

import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.sundy.lingbao.common.consts.Consts;
import com.sundy.lingbao.common.util.DateHelper;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

	@Id
	@GeneratedValue
	private Long id;
	
	private String createBy;
	
	private Date createDate;
	
	private String updateBy;
	
	private Date updateDate;
	
	private Integer state;
	
	@PrePersist
	public void prePersist() {
		if (Objects.isNull(createDate)) {
			createDate = DateHelper.now();
		}
		
		if (Objects.isNull(updateDate)) {
			updateDate = createDate;
		}
		
		if (Objects.isNull(state)) {
			state = Consts.State.ACTIVE.getCode();
		}
		
	}
	
	@PreUpdate
	public void preUpdate() {
		if (Objects.isNull(updateDate)) {
			updateDate = DateHelper.now();
		}
	}
	
	
}
