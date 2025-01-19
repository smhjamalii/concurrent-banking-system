package com.azkivam.bank.aspect.log;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class TransactionLogger {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Pointcut("execution(* com.azkivam.bank.service.transaction.TransactionService.*(com.azkivam.bank.dto.TransactionData))")
	public void logDepositAndWithdraw() {
		
	}

	@Pointcut("execution(* com.azkivam.bank.service.transaction.TransactionService.*(com.azkivam.bank.dto.TransferTransactionData))")
	public void logTransfer() {

	}

	@AfterReturning(value = "logDepositAndWithdraw()", argNames = "joinPoint")
	public void afterDepositOrWithdraw(JoinPoint joinPoint){
		applicationEventPublisher.publishEvent(joinPoint.getArgs()[0]);
	}

	@AfterReturning(value = "logTransfer()", argNames = "joinPoint")
	public void afterTransfer(JoinPoint joinPoint){
		applicationEventPublisher.publishEvent(joinPoint.getArgs()[0]);
	}

}
