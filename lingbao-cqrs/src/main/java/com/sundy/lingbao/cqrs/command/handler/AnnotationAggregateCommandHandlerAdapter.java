package com.sundy.lingbao.cqrs.command.handler;

import com.sundy.lingbao.cqrs.aggregate.AnnotatedAggregrateInspector;
import com.sundy.lingbao.cqrs.aggregate.AnnotationCommandTargetResolver;
import com.sundy.lingbao.cqrs.aggregate.CommandTargetResolver;
import com.sundy.lingbao.cqrs.command.bus.CommandBus;
import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.repository.AggregateRepository;
import com.sundy.lingbao.cqrs.unitofwork.UnitOfWork;

public class AnnotationAggregateCommandHandlerAdapter implements CommandHandler<CommandMessage<?>> {

	private final AnnotatedAggregrateInspector<?> inspector;
	 
	public <T> AnnotationAggregateCommandHandlerAdapter(Class<T> aggregateClass, AggregateRepository<T> repository, CommandBus commandBus) {
		this(aggregateClass, repository, commandBus, new AnnotationCommandTargetResolver());
	}
	
	public <T> AnnotationAggregateCommandHandlerAdapter(Class<T> aggregateClass, AggregateRepository<T> repository, CommandBus commandBus, CommandTargetResolver commandTargetResolver) {
		inspector = AnnotatedAggregrateInspector.inspector(aggregateClass, repository, commandTargetResolver);
		for (String commandName : inspector.getSupportCommands()) {
			commandBus.subscribe(commandName, this);
		}
	}
	
	@Override
	public Object handle(CommandMessage<?> commandMessage, UnitOfWork<CommandMessage<?>> unitOfWork) {
		return inspector.getCommandHandler(commandMessage.getCommandName()).handle(commandMessage);
	}

	
}
