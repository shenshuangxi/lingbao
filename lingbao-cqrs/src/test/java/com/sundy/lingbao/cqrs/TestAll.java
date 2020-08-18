package com.sundy.lingbao.cqrs;

import org.junit.Before;
import org.junit.Test;

import com.sundy.lingbao.cqrs.aggregate.User;
import com.sundy.lingbao.cqrs.command.AddUserAge;
import com.sundy.lingbao.cqrs.command.CreateUser;
import com.sundy.lingbao.cqrs.command.UpdateUserName;
import com.sundy.lingbao.cqrs.command.bus.CommandBus;
import com.sundy.lingbao.cqrs.command.gateway.CommandGateway;
import com.sundy.lingbao.cqrs.command.gateway.SimpleCommandGateway;
import com.sundy.lingbao.cqrs.command.handler.AnnotationAggregateCommandHandlerAdapter;
import com.sundy.lingbao.cqrs.event.bus.EventBus;
import com.sundy.lingbao.cqrs.event.bus.MasterEventBus;
import com.sundy.lingbao.cqrs.event.listeners.EventListenerAdapter;
import com.sundy.lingbao.cqrs.event.listeners.UserListener;
import com.sundy.lingbao.cqrs.repository.EventSourceRepository;
import com.sundy.lingbao.cqrs.repository.AggregateRepository;
import com.sundy.lingbao.cqrs.store.EventStore;
import com.sundy.lingbao.cqrs.store.FileEventStore;

public class TestAll {

	private CommandBus commandBus;
	private CommandGateway commandGateway;
	private AggregateRepository<User> repository;
	private EventStore eventStore;
	private EventBus eventBus;
	
	@Before
	public void init() {
//		this.commandBus = new SimpleCommandBus();
//		this.commandGateway = new SimpleCommandGateway(this.commandBus);
//		this.eventStore = new FileEventStore();
//		this.eventBus = new MasterEventBus(0, eventStore);
//		this.repository = new EventSourceRepository<>(this.eventStore, this.eventBus);
//		new AnnotationAggregateCommandHandlerAdapter(User.class, repository, commandBus);
//		new EventListenerAdapter(new UserListener(), this.eventBus);
	}
	
	@Test
	public void testCommand() {
		
		long startTime = System.currentTimeMillis();
		int threadCount = 1000;
		Thread[] threads = new Thread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			threads[i] = new Thread(){
				@Override
				public void run() {
					for (int j = 0; j < 10; j++) {
						String identifier = TestAll.this.commandGateway.sendAndWait(new CreateUser("小米", 12));
						TestAll.this.commandGateway.send(new AddUserAge(identifier, 2));
						TestAll.this.commandGateway.send(new UpdateUserName(identifier, "大米"));
					}
				}
				
			};
			threads[i].start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long time = (System.currentTimeMillis()-startTime);
		System.out.println("耗时: "+time);
		System.exit(0);
//		this.commandGateway.sendAndWait(new CreateUser("小米1", 12));
//		this.commandGateway.sendAndWait(new CreateUser("小米2", 12), 10000l, TimeUnit.MILLISECONDS);
	}
	
}
