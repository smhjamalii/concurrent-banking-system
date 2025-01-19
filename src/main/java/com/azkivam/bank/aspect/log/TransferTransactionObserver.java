package com.azkivam.bank.aspect.log;

import com.azkivam.bank.dto.TransferTransactionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component @Slf4j(topic = "transaction-log")
public class TransferTransactionObserver implements TransactionObserver<TransferTransactionData> {

    @EventListener
    @Override
    public void onTransaction(TransferTransactionData data) {
        log.info("Transaction: Withdraw, Bank Account: {}, Amount: {}", data.fromNumber(), (long) data.balance());
        log.info("Transaction: Deposit, Bank Account: {}, Amount: {}", data.toNumber(), (long) data.balance());
    }
}
