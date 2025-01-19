package com.azkivam.bank.service.transaction;

import com.azkivam.bank.dto.TransactionData;
import com.azkivam.bank.dto.TransferTransactionData;

public interface TransactionService {

    void deposit(TransactionData transactionData);

    void withdraw(TransactionData transactionData);

    void transfer(TransferTransactionData transactionData);

    void recoverFailedTransactions();
}
