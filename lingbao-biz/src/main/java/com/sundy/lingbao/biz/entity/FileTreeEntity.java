package com.sundy.lingbao.biz.entity;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;

@Entity
@Getter
@Setter
public class FileTreeEntity extends BaseEntity {

	private String appId;
	
	private String clusterId;
	
	private String fileTreeId;
	
	private String commitKey;
	
	private String comment;
	
	//实际就是 文件名 或目录名
	private String treeName;
	
	//appId+clusterId+UUID
	private String fileKey;
	
}
