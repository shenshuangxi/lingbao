package com.sundy.lingbao.cqrs.aggregate;

public class VersionAggregateIdentifier {

	private final String identifier;
	
	private final Long version;

	public VersionAggregateIdentifier(String identifier, Long version) {
		this.identifier = identifier;
		this.version = version;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Long getVersion() {
		return version;
	}
	
}
