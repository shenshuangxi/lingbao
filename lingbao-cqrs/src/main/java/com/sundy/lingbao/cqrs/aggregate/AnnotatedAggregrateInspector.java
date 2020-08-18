package com.sundy.lingbao.cqrs.aggregate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.core.util.ClassUtil;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.cqrs.aggregate.annotation.CommandHandler;
import com.sundy.lingbao.cqrs.aggregate.annotation.EventHandler;
import com.sundy.lingbao.cqrs.message.CommandMessage;
import com.sundy.lingbao.cqrs.message.EventMessage;
import com.sundy.lingbao.cqrs.message.handler.MessageHandler;
import com.sundy.lingbao.cqrs.repository.AggregateRepository;
import com.sundy.lingbao.cqrs.unitofwork.CurrentUnitOfWork;


/**
 * 根据实际传入的聚合根 获取所有的 commandhandler  eventhandler  以及构造方法的 command
 * @author Administrator
 *
 */
public final class AnnotatedAggregrateInspector<T> {
	
	private final static Logger logger = LoggerFactory.getLogger(AnnotatedAggregrateInspector.class);

	private final AggregateRepository<T> repository;
	
	private final String aggregateType;
	
	private final CommandTargetResolver commandTargetResolver;
	
	private final Map<String, MessageHandler<CommandMessage<?>>> commandHandlers = new HashMap<String, MessageHandler<CommandMessage<?>>>();
	
	private final List<MessageHandler<EventMessage<?>>> eventHandlers = new ArrayList<MessageHandler<EventMessage<?>>>();
	
	private AnnotatedAggregrateInspector(Class<T> aggregateClass, AggregateRepository<T> repository, CommandTargetResolver commandTargetResolver) {
		this.repository = repository;
		this.commandTargetResolver = commandTargetResolver;
		this.aggregateType = aggregateClass.getName();
		initializeHandlers(aggregateClass);
	}
	
	@SuppressWarnings("unchecked")
	private void initializeHandlers(Class<T> aggregateClass) {
		List<Method> methods = ClassUtil.findAllMethods(aggregateClass);
		for (Method method : methods) {
			if (method.isAnnotationPresent(CommandHandler.class)) {
				commandHandlers.put(method.getParameterTypes()[0].getName(), new AnnotatedAggregateCommandMessageHandler(method));
			} else if (method.isAnnotationPresent(EventHandler.class)) {
				eventHandlers.add(new AnnotatedAggregateEventMessageHandler(method));
			}
		}
		Constructor<T>[] constructors = (Constructor<T>[]) aggregateClass.getConstructors();
		for (Constructor<T> constructor : constructors) {
			if (constructor.isAnnotationPresent(CommandHandler.class)) {
				commandHandlers.put(constructor.getParameterTypes()[0].getName(), new AnnotatedAggregateConstructorCommandMessageHandler(constructor));
			}
		}
	}
	
	public MessageHandler<CommandMessage<?>> getCommandHandler(String commandName) {
		return commandHandlers.get(commandName);
	}
	
	public Collection<String> getSupportCommands() {
		return commandHandlers.keySet();
	}

	public static <T> AnnotatedAggregrateInspector<T> inspector(Class<T> aggregateClass, AggregateRepository<T> repository) {
		return new AnnotatedAggregrateInspector<T>(aggregateClass, repository, new AnnotationCommandTargetResolver());
	}
	
	public static <T> AnnotatedAggregrateInspector<T> inspector(Class<T> aggregateClass, AggregateRepository<T> repository, CommandTargetResolver commandTargetResolver) {
		return new AnnotatedAggregrateInspector<T>(aggregateClass, repository, commandTargetResolver);
	}
	
	
	private class AnnotatedAggregateConstructorCommandMessageHandler implements MessageHandler<CommandMessage<?>> {
		
		private final Constructor<T> constructor;
		
		public AnnotatedAggregateConstructorCommandMessageHandler(Constructor<T> constructor) {
			this.constructor = constructor;
		}

		@Override
		public Object handle(CommandMessage<?> commandMessage) {
			AggregateRoot<T> aggregate = repository.newInstance(()->constructor.newInstance(commandMessage.getPayload()));
			CurrentUnitOfWork.get().setAggregate(aggregate);
			return aggregate;
		}
	}
	
	
	public class AnnotatedAggregateCommandMessageHandler implements MessageHandler<CommandMessage<?>> {
		private final Method method;
		
		public AnnotatedAggregateCommandMessageHandler(Method method) {
			this.method = method;
		}

		@Override
		public Object handle(CommandMessage<?> commandMessage) {
			AggregateRoot<T> aggregate = null;
			VersionAggregateIdentifier versionAggregateIdentifier = commandTargetResolver.resolveTarget(commandMessage);
			if (!StringUtils.isNullOrEmpty(versionAggregateIdentifier.getIdentifier())) {
				if (versionAggregateIdentifier.getVersion()==null) {
					aggregate = repository.load(aggregateType, versionAggregateIdentifier.getIdentifier());
				} else {
					aggregate = repository.load(aggregateType, versionAggregateIdentifier.getIdentifier(), versionAggregateIdentifier.getVersion());
				}
			}
			if (aggregate==null) {
				throw new LingbaoException(-1, "not found the aggregrate");
			}
			try {
				CurrentUnitOfWork.get().setAggregate(aggregate);
				return method.invoke(aggregate.getAggregatePayload(), commandMessage.getPayload());
			} catch (Throwable e) {
				logger.error("operate command fail", e);
				throw new LingbaoException(-1, "operate command fail");
			}
		}
		
	} 
	
	private class AnnotatedAggregateEventMessageHandler implements MessageHandler<EventMessage<?>> {
		private final Method method;
		public AnnotatedAggregateEventMessageHandler(Method method) {
			this.method = method;
		}
		
		@Override
		public boolean match(EventMessage<?> message) {
			return this.method.getParameterTypes()[0].getName().equals(message.getPayloadType());
		}

		@Override
		public Object handle(EventMessage<?> eventMessage) {
			try {
				Object realAggregate = CurrentUnitOfWork.get().getAggregate().getAggregatePayload();
				Object ret = method.invoke(realAggregate, eventMessage.getPayload());
				return ret;
			} catch (Throwable e) {
				logger.error("operate event fail", e);
				throw new LingbaoException(-1, "operate event fail");
			}
		}
	}

	public MessageHandler<EventMessage<?>> findEventHandler(EventMessage<?> eventMessage) {
		for (MessageHandler<EventMessage<?>> handler : eventHandlers) {
			if (handler.match(eventMessage)) {
				return handler;
			}
		}
		throw new LingbaoException(-1, "no handle for event message");
	}

	public void publishEventMessage(EventMessage<?> eventMessage) {
		EventMessage<?> persistEventMessage = repository.getEventStore().appendEvent(eventMessage);
		repository.getEventBus().publish(persistEventMessage);
	} 
	
	
}
