package com.sundy.lingbao.cqrs.command;


import lombok.Getter;

@Getter
public class CreateUser {

	private String name;
	
	private Integer age;
	
	public CreateUser(String name, Integer age) {
		this.name = name;
		this.age = age;
	}



}
