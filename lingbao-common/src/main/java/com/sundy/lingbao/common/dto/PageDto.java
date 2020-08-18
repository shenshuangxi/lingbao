package com.sundy.lingbao.common.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageDto<T> {

	private List<T> rows;
	private Long total;
	
}
