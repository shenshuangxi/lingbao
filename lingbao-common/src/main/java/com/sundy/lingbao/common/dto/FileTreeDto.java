package com.sundy.lingbao.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileTreeDto extends BussinessDto {

	private String fileTreeId;
	
	private String commitKey;
	
	private String comment;
	
	private String treeName;
	
	//appId+clusterId+UUID
	private FileSnapshotDto fileSnapshotDto;
	
}
