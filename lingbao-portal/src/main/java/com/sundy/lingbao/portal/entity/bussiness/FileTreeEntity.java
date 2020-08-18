package com.sundy.lingbao.portal.entity.bussiness;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import com.sundy.lingbao.portal.entity.BussinessEntity;


@Entity
@Getter
@Setter
public class FileTreeEntity extends BussinessEntity {

	private String fileTreeId;
	
	private String commitKey;
	
	private String comment;
	
	//实际就是 文件名 或目录名
	private String treeName;
	
	//appId+clusterId+UUID
	private String fileKey;
	
}
