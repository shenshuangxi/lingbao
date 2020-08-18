package com.sundy.lingbao.biz.entity;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.BaseEntity;
import com.sundy.lingbao.common.entity.converts.MapConvert;

@Entity
@Getter
@Setter
public class FileSnapshotEntity extends BaseEntity {

	private String appId;
	
	private String clusterId;
	
	private String fileKey;
	
	//行号-行内容
	@Convert(converter=MapConvert.class)
	private Map<Integer, String> contents;

}
