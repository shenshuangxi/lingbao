package com.sundy.lingbao.common.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileSnapshotDto extends BussinessDto {

	private String fileKey;
	
	//行号-行内容
	private Map<Integer, String> contents;
	
}
