package com.azkivam.bank.service.transaction.command;

import com.azkivam.bank.baseinfo.TransactionType;
import com.azkivam.bank.dto.TransferTransactionData;
import com.azkivam.bank.service.transaction.TransactionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class TransferCommand implements TransactionCommand {

    private final TransactionService transactionService;
    private final String fromNumber;
    private final String toNumber;
    private final double balance;

    @Override
    public void execute() {

        TransferTransactionData transactionData = new TransferTransactionData(fromNumber, toNumber, TransactionType.TRANSFER, balance);

        transactionService.transfer(transactionData);
    }
}
