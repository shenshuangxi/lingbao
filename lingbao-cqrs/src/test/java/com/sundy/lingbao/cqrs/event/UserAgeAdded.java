package com.sundy.lingbao.cqrs.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAgeAdded {

	private String aggregateIdentifier;
	
	private Integer increaseAge;

	public UserAgeAdded(String aggregateIdentifier, Integer increaseAge) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.increaseAge = increaseAge;
	}
	
	public UserAgeAdded() {
	}

	
	
	
	
}
