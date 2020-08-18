package com.sundy.lingbao.core.cqrs.repository;

public interface Repository {

	Object loadAggregate(String identifier);

	void saveAggregate(Object aggregate);

}
