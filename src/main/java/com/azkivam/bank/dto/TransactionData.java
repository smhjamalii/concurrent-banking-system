package com.azkivam.bank.dto;

import com.azkivam.bank.baseinfo.TransactionType;
import lombok.Builder;

@Builder
public record TransactionData(String number, TransactionType type, double balance) {
}
