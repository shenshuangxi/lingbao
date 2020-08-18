package com.sundy.lingbao.portal.dto;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Search {

	private Integer pn;
	
	private Integer ps;
	
	private String userName;
	
	private String name;
	
	private String orgName;
	
	private String roleName;
	
	public boolean hasPage() {
		return Objects.nonNull(pn) && Objects.nonNull(ps);
	}
	
}
