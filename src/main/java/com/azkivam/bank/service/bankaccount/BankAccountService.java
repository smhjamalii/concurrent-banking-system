package com.azkivam.bank.service.bankaccount;

import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.service.CrudService;

import java.util.Optional;

public interface BankAccountService extends CrudService<BankAccount> {

    Optional<BankAccount> findByHolderUsername(String username);

    Optional<BankAccount> findByNumber(String number);

}
