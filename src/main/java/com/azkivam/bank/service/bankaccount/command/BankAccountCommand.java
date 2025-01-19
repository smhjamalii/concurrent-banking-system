package com.azkivam.bank.service.bankaccount.command;

public interface BankAccountCommand<T> {

    T execute();

}
