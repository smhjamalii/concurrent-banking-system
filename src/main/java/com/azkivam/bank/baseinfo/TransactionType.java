package com.azkivam.bank.baseinfo;

public enum TransactionType {

    DEPOSIT("deposit"), WITHDRAW("withdraw"), TRANSFER("transfer");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
