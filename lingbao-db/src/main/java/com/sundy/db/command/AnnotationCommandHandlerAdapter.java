package com.sundy.db.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationCommandHandlerAdapter<C> implements CommandHandler<C> {

	private final Map<String, Method> handlers = new HashMap<String, Method>();
	private final Object target;
	 
	public AnnotationCommandHandlerAdapter(Object target, CommandBus commandBus) {
		this.target = target;
		Method[] methods = this.target.getClass().getMethods();
		for(Method method : methods) {
			if(method.isAnnotationPresent(com.sundy.db.command.annotation.CommandHandler.class)){
				String commandName = method.getParameterTypes()[0].getName();
				handlers.put(commandName, method);
				commandBus.subscribe(commandName, this);
			}
		}
	}
	
	@Override
	public Object handle(C command) throws Exception {
		Method method = handlers.get(command.getClass().getName());
		return method.invoke(target, command);
	}

}
