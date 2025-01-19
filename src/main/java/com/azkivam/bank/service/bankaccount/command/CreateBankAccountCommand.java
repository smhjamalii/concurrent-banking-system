package com.azkivam.bank.service.bankaccount.command;

import com.azkivam.bank.baseinfo.TransactionType;
import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.dto.TransactionData;
import com.azkivam.bank.dto.User;
import com.azkivam.bank.exception.BusinessException;
import com.azkivam.bank.service.bankaccount.BankAccountService;
import com.azkivam.bank.service.transaction.TransactionService;
import com.azkivam.bank.service.user.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateBankAccountCommand implements BankAccountCommand<BankAccount> {

    private final BankAccountService bankAccountService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final String holderUsername;
    private final double initialBalance;

    @Override
    public BankAccount execute() {

        Optional<User> holder = userService.findByUsername(holderUsername);

        if(holder.isEmpty()) throw new BusinessException("User not found!");

        BankAccount bankAccount = BankAccount.builder()
                .holder(holder.get())
                .balance(0)
                .number(UUID.randomUUID().toString())
                .build();

        bankAccount = bankAccountService.save(bankAccount);

        TransactionData transactionData = new TransactionData(bankAccount.number(), TransactionType.DEPOSIT, initialBalance);
        transactionService.deposit(transactionData);

        return BankAccount.builder()
                .id(bankAccount.id())
                .holder(holder.get())
                .balance(initialBalance)
                .number(bankAccount.number())
                .build();
    }
}
