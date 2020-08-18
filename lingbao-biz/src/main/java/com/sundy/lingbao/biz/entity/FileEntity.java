package com.sundy.lingbao.biz.entity;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.sundy.lingbao.biz.util.KeyUtil;
import com.sundy.lingbao.common.entity.BaseEntity;
import com.sundy.lingbao.common.entity.converts.MapConvert;

import lombok.Getter;
import lombok.Setter;


/**
 * 对应某个集群中的一个文件 存储 最新的 该集群中文件的最新版本
 * @author Administrator
 *
 */

@Entity
@Getter
@Setter
public class FileEntity extends BaseEntity {
	
	private String fileKey;
	
	private String appId;

	private String clusterId;
	
	//文件名称
	private String name;
	
	//行号-行内容
	@Convert(converter=MapConvert.class)
	private Map<Integer, String> contents;
	
	@PrePersist
	public void preFilePersisit() {
		this.fileKey = KeyUtil.getFileKey(contents);
	}
	
	@PreUpdate
	public void preFileUpdate() {
		this.fileKey = KeyUtil.getFileKey(contents);
	}
	
	
	
}
