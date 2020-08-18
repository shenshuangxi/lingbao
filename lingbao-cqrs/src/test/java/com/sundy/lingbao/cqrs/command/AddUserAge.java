package com.sundy.lingbao.cqrs.command;

import com.sundy.lingbao.cqrs.aggregate.annotation.TargetAggregateIdentifier;

import lombok.Getter;

@Getter
public class AddUserAge {

	@TargetAggregateIdentifier
	private final String aggregateIdentifier;
	
	private final Integer increaseAge;

	public AddUserAge(String aggregateIdentifier, Integer increaseAge) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.increaseAge = increaseAge;
	}
	
	
	
}
