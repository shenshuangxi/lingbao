package com.sundy.lingbao.cqrs.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreated {

	private String aggregateIdentifier;
	
	private String name;
	
	private Integer age;

	public UserCreated() {}
	
	public UserCreated(String name, Integer age, String aggregateIdentifier) {
		this.name = name;
		this.age = age;
		this.aggregateIdentifier = aggregateIdentifier;
	}
	
	
	
}
