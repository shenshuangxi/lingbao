package com.sundy.lingbao.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.sundy.lingbao.core.tracer.Tracer;
import com.sundy.lingbao.core.tracer.spi.Transaction;

@Aspect
@Component
public class RepositoryAop {

	@Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
	public void anyRepositoryMethod() {}

	@Around("anyRepositoryMethod()")
	public Object invokeWithCatTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
	    String name = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
	    Transaction catTransaction = Tracer.newTransaction("SQL", name);
	    try {
	    	Object result = joinPoint.proceed();
	    	catTransaction.setStatus(Transaction.SUCCESS);
	    	return result;
	    } catch (Throwable ex) {
	    	catTransaction.setStatus(ex);
	    	throw ex;
	    } finally {
	    	catTransaction.complete();
	    }
	}
	
}
