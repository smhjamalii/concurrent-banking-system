package com.azkivam.bank.dto.mapper;

import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.model.BankAccountModel;

import java.util.function.Function;

public class BankAccountMapper {

    public static final Function<BankAccountModel, BankAccount> toDTO =
            model -> new BankAccount(model.getId(), model.getNumber(),
                    UserMapper.toDTO.apply(model.getHolder()),
                    model.getBalance(),
                    model.getVersion());

    public static final Function<BankAccount, BankAccountModel> toModel =
            dto -> new BankAccountModel(dto.id(), dto.number(),
                    UserMapper.toModel.apply(dto.holder()),
                    dto.balance(),
                    dto.version());

}
