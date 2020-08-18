package com.sundy.lingbao.cqrs.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNameUpdated {

	private String aggregateIdentifier;
	
	private String name;

	public UserNameUpdated(String aggregateIdentifier, String name) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.name = name;
	}
	
	public UserNameUpdated() {
	}
	
}
