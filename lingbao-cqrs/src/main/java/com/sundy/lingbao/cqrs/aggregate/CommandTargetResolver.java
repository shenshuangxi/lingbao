package com.sundy.lingbao.cqrs.aggregate;

import com.sundy.lingbao.cqrs.message.CommandMessage;

public interface CommandTargetResolver {

	VersionAggregateIdentifier resolveTarget(CommandMessage<?> commandMessage);
	
}
