package com.sundy.lingbao.cqrs.command;

import com.sundy.lingbao.cqrs.aggregate.annotation.TargetAggregateIdentifier;

import lombok.Getter;

@Getter
public class UpdateUserName {

	@TargetAggregateIdentifier
	private final String aggregateIdentifier;
	
	private final String name;

	public UpdateUserName(String aggregateIdentifier, String name) {
		this.aggregateIdentifier = aggregateIdentifier;
		this.name = name;
	}
	
	
	
}
