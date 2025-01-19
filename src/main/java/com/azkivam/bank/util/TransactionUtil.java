package com.azkivam.bank.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TransactionUtil {

    private final TransactionTemplate transactionTemplate;

    public void doInTransaction(Runnable action) {

        transactionTemplate.executeWithoutResult(status -> action.run());
    }

}
