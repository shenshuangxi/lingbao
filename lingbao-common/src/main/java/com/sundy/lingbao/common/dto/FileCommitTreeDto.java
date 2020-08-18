package com.sundy.lingbao.common.dto;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileCommitTreeDto extends BussinessDto {

	private String parentCommitKey;
	
	private String commitKey;
	
	private String comment;
	
	//存储的是文件名称或者文件中的文件夹
	private Collection<FileTreeDto> childrenFileTreeDtos;
	
}
