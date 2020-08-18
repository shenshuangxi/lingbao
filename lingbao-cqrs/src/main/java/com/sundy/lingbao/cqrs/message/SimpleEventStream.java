package com.sundy.lingbao.cqrs.message;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

public class SimpleEventStream implements EventStream {

	private static final EventStream EMPTY_STREAM = new SimpleEventStream();

    private int nextIndex;
    private final EventMessage<?>[] events;
    
    public SimpleEventStream(Collection<? extends EventMessage<?>> events) {
        this(events.toArray(new EventMessage[events.size()]));
    }
    
    public SimpleEventStream(EventMessage<?>... events) {
        this.events = Arrays.copyOfRange(events, 0, events.length);
    }
	
	@Override
	public boolean hasNext() {
		return events.length > nextIndex;
	}

	@Override
	public EventMessage<?> next() {
		if (!hasNext()) {
            throw new NoSuchElementException("Trying to peek beyond the limits of this stream.");
        }
        return events[nextIndex++];
	}

	@Override
	public EventMessage<?> peek() {
		if (!hasNext()) {
            throw new NoSuchElementException("Trying to peek beyond the limits of this stream.");
        }
        return events[nextIndex];
	}
	
	public static EventStream emptyStream() {
        return EMPTY_STREAM;
    }

}
