package com.azkivam.bank.repository;

import com.azkivam.bank.model.BankAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountModel, Long> {

    Optional<BankAccountModel> findByHolderUsername(String username);

    Optional<BankAccountModel> findByNumber(String number);

}
