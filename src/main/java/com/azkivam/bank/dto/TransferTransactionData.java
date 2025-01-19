package com.azkivam.bank.dto;

import com.azkivam.bank.baseinfo.TransactionType;

public record TransferTransactionData(String fromNumber, String toNumber, TransactionType type, double balance) {
}
