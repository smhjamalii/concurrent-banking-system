package com.azkivam.bank.aspect.log;

import com.azkivam.bank.dto.TransactionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component @Slf4j(topic = "transaction-log")
public class DepositTransactionObserver implements TransactionObserver<TransactionData> {

    @EventListener
    @Override
    public void onTransaction(TransactionData data) {
        log.info("Transaction: {}, Bank Account: {}, Amount: {}", data.type(), data.number(), (long) data.balance());
    }
}
