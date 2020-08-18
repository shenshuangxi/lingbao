package com.sundy.lingbao.cqrs.command.callback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.cqrs.message.CommandMessage;


public class FutureCommandCallback<C, R> extends CompletableFuture<R> implements CommandCallback<C, R> {

	private final static Logger logger = LoggerFactory.getLogger(FutureCommandCallback.class);
	
	private CommandMessage<C> commandMessage;
	
	@Override
	public void onSuccess(CommandMessage<C> commandMessage, R result) {
		super.complete(result);
		this.commandMessage = commandMessage;
	}

	@Override
	public void onFail(CommandMessage<C> commandMessage, Throwable cause) {
		super.completeExceptionally(cause);
		this.commandMessage = commandMessage;
		
	}

	@Override
	public R getResult() {
		try {
			return super.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
			throw new LingbaoException(-1, e.getMessage());
		}
		return null;
	}

	@Override
	public CommandMessage<C> getCommandMessage() {
		return commandMessage;
	}

	@Override
	public R getResult(long timeout, TimeUnit timeUnit) {
		try {
			return super.get(timeout, timeUnit);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
			throw new LingbaoException(-1, e.getMessage());
		} catch (TimeoutException e) {
			logger.warn(e.getMessage(), e);
		}
		return null;
	}


}
