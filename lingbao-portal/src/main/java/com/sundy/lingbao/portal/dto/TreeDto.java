package com.sundy.lingbao.portal.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreeDto {

	private Long id;
	
	private Long pid;
	
	private String name;
	
	private String text;
	
	private String state;
	
	private String chekced;
	
	private String remark;
	
	private Map<String, Object> attributes;
	
	private List<TreeDto> children;
	
	
}
