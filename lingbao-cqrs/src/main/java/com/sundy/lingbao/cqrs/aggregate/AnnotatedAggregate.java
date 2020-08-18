package com.sundy.lingbao.cqrs.aggregate;

import java.util.Collection;

import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.repository.AggregateIdentifierResolver;
import com.sundy.lingbao.cqrs.repository.AggregateRepository;

public class AnnotatedAggregate<T> implements AggregateRoot<T> {

	private String aggregateType;
	
	private T aggregatePayload;
	
	private String aggregateIdentifier;
	
	private Long  aggregateVersion = 0l;
	
	private AnnotatedAggregrateInspector<T> inspector;
	
	public AnnotatedAggregate(AggregateRepository<T> repository, T payload) {
		this(repository, payload, new AnnotationCommandTargetResolver());
	}
	
	@SuppressWarnings("unchecked")
	public AnnotatedAggregate(AggregateRepository<T> repository, T payload, CommandTargetResolver commandTargetResolver) {
		this.inspector = AnnotatedAggregrateInspector.inspector((Class<T>)payload.getClass(), repository);
		this.aggregateType = payload.getClass().getName();
		this.aggregatePayload = payload;
	}
	
	@Override
	public String getAggregateType() {
		return this.aggregateType;
	}

	@Override
	public T getAggregatePayload() {
		return this.aggregatePayload;
	}

	@Override
	public Long getAggregateVersion() {
		return this.aggregateVersion;
	}
	
	@Override
	public String aggregateIdentifier() {
		return aggregateIdentifier;
	}


	@Override
	public Object handle(CommandMessage<?> commandMessage) {
		return inspector.getCommandHandler(commandMessage.getCommandName()).handle(commandMessage);
	}
	
	@Override
	public Object handle(EventMessage<?> eventMessage) {
		Object ret = inspector.findEventHandler(eventMessage).handle(eventMessage);
		this.aggregateIdentifier = AggregateIdentifierResolver.resolveTarget(this.aggregatePayload).getIdentifier();
		this.aggregateVersion = this.aggregateVersion + 1;
		return ret;
	}


	@Override
	public Collection<String> getSupportCommands() {
		return inspector.getSupportCommands();
	}

	@Override
	public void publish(EventMessage<?> eventMessage) {
		handle(eventMessage);
		eventMessage.setSequenceNumber(this.aggregateVersion);
		eventMessage.setAggregateIdentifier(this.aggregateIdentifier);
		eventMessage.setAggregateType(this.aggregateType);
		inspector.publishEventMessage(eventMessage);
	}

}
