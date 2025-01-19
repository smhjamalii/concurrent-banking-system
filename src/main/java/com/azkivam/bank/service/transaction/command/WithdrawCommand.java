package com.azkivam.bank.service.transaction.command;

import com.azkivam.bank.baseinfo.TransactionType;
import com.azkivam.bank.dto.TransactionData;
import com.azkivam.bank.service.transaction.TransactionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class WithdrawCommand implements TransactionCommand {

    private final TransactionService transactionService;
    private final String number;
    private final double balance;

    @Override
    public void execute() {

        TransactionData transactionData = new TransactionData(number, TransactionType.WITHDRAW, balance);

        transactionService.withdraw(transactionData);
    }
}
