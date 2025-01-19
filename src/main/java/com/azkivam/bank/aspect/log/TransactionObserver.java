package com.azkivam.bank.aspect.log;

public interface TransactionObserver<T> {

	void onTransaction(T data);
	
}
