package com.azkivam.bank.repository;

import com.azkivam.bank.baseinfo.TransactionType;
import com.azkivam.bank.model.FailedTransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailedTransactionRepository extends JpaRepository<FailedTransactionModel, Long> {

    List<FailedTransactionModel> findFirst100ByRecoveredIsFalseAndType(TransactionType type);

}
