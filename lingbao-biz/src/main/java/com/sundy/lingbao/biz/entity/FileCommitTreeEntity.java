package com.sundy.lingbao.biz.entity;

import java.util.Set;

import javax.persistence.Convert;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;
import com.sundy.lingbao.common.entity.converts.SetConvert;

@Entity
@Getter
@Setter
public class FileCommitTreeEntity extends BaseEntity {

	private String appId;
	
	private String clusterId;
	
	private String parentCommitKey;
	
	private String commitKey;
	
	private String comment;
	
	//存储的是文件名称或者文件中的文件夹
	@Convert(converter=SetConvert.class)
	private Set<String> childrenFileTreeIds;
	
}
