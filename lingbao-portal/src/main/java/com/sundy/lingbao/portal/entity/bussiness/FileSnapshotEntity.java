package com.sundy.lingbao.portal.entity.bussiness;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

import com.sundy.lingbao.common.entity.converts.MapConvert;
import com.sundy.lingbao.portal.entity.BussinessEntity;

@Entity
@Getter
@Setter
public class FileSnapshotEntity extends BussinessEntity {

	private String fileKey;
	
	//行号-行内容
	@Convert(converter=MapConvert.class)
	private Map<Integer, String> contents;

}
