package com.azkivam.bank.service.bankaccount.command;

import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.service.bankaccount.BankAccountService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class FindBankAccountCommand implements BankAccountCommand<Optional<BankAccount>> {

    private final BankAccountService bankAccountService;
    private final String number;

    @Override
    public Optional<BankAccount> execute() {

        return bankAccountService.findByNumber(number);
    }
}
