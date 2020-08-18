package com.sundy.lingbao.common.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto extends BussinessDto {

	private String fileKey;
	
	//文件名称
	private String name;
	
	//行号-行内容
	private Map<Integer, String> contents;
	
}
