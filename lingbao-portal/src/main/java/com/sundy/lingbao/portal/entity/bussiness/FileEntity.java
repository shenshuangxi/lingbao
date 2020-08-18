package com.sundy.lingbao.portal.entity.bussiness;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.sundy.lingbao.common.entity.converts.MapConvert;
import com.sundy.lingbao.portal.entity.BussinessEntity;
import com.sundy.lingbao.portal.util.KeyUtil;

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
public class FileEntity extends BussinessEntity {
	
	private String fileKey;
	
	//文件名称
	@Column(nullable=false)
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
