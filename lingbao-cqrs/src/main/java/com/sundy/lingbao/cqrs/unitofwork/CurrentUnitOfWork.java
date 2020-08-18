package com.sundy.lingbao.cqrs.unitofwork;

import java.util.Deque;
import java.util.LinkedList;

public class CurrentUnitOfWork {

	private static final ThreadLocal<Deque<UnitOfWork<?>>> CURRENT = new ThreadLocal<Deque<UnitOfWork<?>>>();
	
	public static boolean isStart() {
		return CURRENT.get()!=null && !CURRENT.get().isEmpty();
	}
	
	public static UnitOfWork<?> get(){
		if (isStart()) {
			return CURRENT.get().peek();
		}
		throw new IllegalStateException("No UnitOfWork is currently started for this thread.");
	}
	
	public static void set(UnitOfWork<?> unitOfWork) {
		if(CURRENT.get()==null) {
			CURRENT.set(new LinkedList<UnitOfWork<?>>());
		} 
		CURRENT.get().push(unitOfWork);
	}
	
	public static void clear(UnitOfWork<?> unitOfWork){
		if (isStart()) {
			if (unitOfWork==CURRENT.get().peek()) {
				CURRENT.get().poll();
				if (CURRENT.get().isEmpty()) {
					CURRENT.remove();
				}
				return;
			} else {
				throw new IllegalStateException("Could not clear this UnitOfWork. It is not the active one.");
			}
		}
		throw new IllegalStateException("No UnitOfWork is currently started for this thread.");
	}
	
}
