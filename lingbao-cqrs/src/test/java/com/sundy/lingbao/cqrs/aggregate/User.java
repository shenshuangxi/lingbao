package com.sundy.lingbao.cqrs.aggregate;

import com.sundy.lingbao.core.idgenerate.IdGenerateFactory;
import com.sundy.lingbao.cqrs.aggregate.annotation.AggregateIdentifier;
import com.sundy.lingbao.cqrs.aggregate.annotation.CommandHandler;
import com.sundy.lingbao.cqrs.aggregate.annotation.EventHandler;
import com.sundy.lingbao.cqrs.command.CreateUser;
import com.sundy.lingbao.cqrs.command.UpdateUserName;
import com.sundy.lingbao.cqrs.command.AddUserAge;
import com.sundy.lingbao.cqrs.event.UserAgeAdded;
import com.sundy.lingbao.cqrs.event.UserCreated;
import com.sundy.lingbao.cqrs.event.UserNameUpdated;

public class User {

	private String name;
	
	private Integer age;
	
	@AggregateIdentifier
	private String aggregateIdentifier;
	
	
	
	public User() {
	}

	@CommandHandler
	public User(CreateUser createUser) {
		UserCreated userCreated = new UserCreated(createUser.getName(), createUser.getAge(), IdGenerateFactory.getIdGenerator().getId());
		com.sundy.lingbao.cqrs.aggregate.AggregateRoot.dispatchEvent(userCreated);
	}
	
	@CommandHandler
	public void handler(AddUserAge addUserAge) {
		UserAgeAdded userAgeAdded = new UserAgeAdded(addUserAge.getAggregateIdentifier(), addUserAge.getIncreaseAge());
		AggregateRoot.dispatchEvent(userAgeAdded);
	}
	
	@CommandHandler
	public void handler(UpdateUserName updateUserName) {
		UserNameUpdated userNameUpdated = new UserNameUpdated(updateUserName.getAggregateIdentifier(), updateUserName.getName());
		AggregateRoot.dispatchEvent(userNameUpdated);
	}
	
	@EventHandler
	public void on(UserCreated userCreated) {
		this.name = userCreated.getName();
		this.age = userCreated.getAge();
		this.aggregateIdentifier = userCreated.getAggregateIdentifier();
	}
	
	@EventHandler
	public void on(UserAgeAdded userAgeAdded) {
		this.age = this.age + userAgeAdded.getIncreaseAge();
	}
	
	@EventHandler
	public void on(UserNameUpdated userNameUpdated) {
		this.name = userNameUpdated.getName();
	}
	
}
