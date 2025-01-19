package com.azkivam.bank.service.transaction;

import com.azkivam.bank.baseinfo.TransactionType;
import com.azkivam.bank.dto.TransactionData;
import com.azkivam.bank.dto.TransferTransactionData;
import com.azkivam.bank.exception.BusinessException;
import com.azkivam.bank.model.BankAccountModel;
import com.azkivam.bank.model.FailedTransactionModel;
import com.azkivam.bank.repository.BankAccountRepository;
import com.azkivam.bank.repository.FailedTransactionRepository;
import com.azkivam.bank.util.TransactionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final FailedTransactionRepository failedTransactionRepository;
    private final TransactionUtil transactionUtil;

    @Transactional(rollbackFor = BusinessException.class)
    @Retryable(backoff = @Backoff(delay = 1000L, multiplier = 2))
    @Override
    public void deposit(TransactionData transactionData) {

        if(transactionData.balance() == 0.0D) return;

        Optional<BankAccountModel> optional = bankAccountRepository.findByNumber(transactionData.number());

        optional.ifPresent(model -> {

            model.setBalance(model.getBalance() + transactionData.balance());
            bankAccountRepository.save(model);
        });
    }

    @Transactional(rollbackFor = BusinessException.class)
    @Retryable(backoff = @Backoff(delay = 1000L, multiplier = 2))
    @Override
    public void withdraw(TransactionData transactionData) {

        if(transactionData.balance() == 0.0D) return;

        Optional<BankAccountModel> optional = bankAccountRepository.findByNumber(transactionData.number());

        optional.ifPresent(model -> {

            verifySufficientBalance(model.getBalance(), transactionData.balance());

            model.setBalance(model.getBalance() - transactionData.balance());
            bankAccountRepository.save(model);
        });
    }

    @Recover
    public void recoverFailedTransferTransaction(Throwable exception, TransactionData transactionData) {

        FailedTransactionModel.FailedTransactionModelBuilder failedTransactionBuilder = FailedTransactionModel.builder()
                .type(transactionData.type())
                .balance(transactionData.balance());

        switch (transactionData.type()) {
            case TransactionType.DEPOSIT -> failedTransactionBuilder.depositAccountNumber(transactionData.number());
            case TransactionType.WITHDRAW -> failedTransactionBuilder.withdrawAccountNumber(transactionData.number());
        }

        failedTransactionRepository.save(failedTransactionBuilder.build());
    }

    @Transactional(rollbackFor = BusinessException.class)
    @Retryable(backoff = @Backoff(delay = 1000L, multiplier = 2))
    @Override
    public void transfer(TransferTransactionData transactionData) {
        if (transactionData.balance() == 0.0D) return;

        Optional<BankAccountModel> fromAccountOptional = bankAccountRepository.findByNumber(transactionData.fromNumber());
        Optional<BankAccountModel> toAccountOptional = bankAccountRepository.findByNumber(transactionData.toNumber());

        if (fromAccountOptional.isPresent() && toAccountOptional.isPresent()) {

            BankAccountModel fromAccount = fromAccountOptional.get();
            BankAccountModel toAccount = toAccountOptional.get();

            verifySufficientBalance(fromAccount.getBalance(), transactionData.balance());

            fromAccount.setBalance(fromAccount.getBalance() - transactionData.balance());
            toAccount.setBalance(toAccount.getBalance() + transactionData.balance());

            bankAccountRepository.save(fromAccount);
            bankAccountRepository.save(toAccount);
        }
    }

    @Recover
    public void recoverFailedTransferTransaction(Throwable exception, TransferTransactionData transactionData) {

        FailedTransactionModel failedTransaction = FailedTransactionModel.builder()
                .type(TransactionType.TRANSFER)
                .depositAccountNumber(transactionData.toNumber())
                .withdrawAccountNumber(transactionData.fromNumber())
                .balance(transactionData.balance())
                .build();

        failedTransactionRepository.save(failedTransaction);
    }

    private void verifySufficientBalance(double currentAccountBalance, double decreasedBalance) {
        if (currentAccountBalance - decreasedBalance < 0) {

            throw new BusinessException("Insufficient account balance!");
        }
    }

    @Scheduled(fixedRate = 10)
    public void recoverFailedTransactions() {
        recoverFailedTransactionsByType(TransactionType.TRANSFER);
        recoverFailedTransactionsByType(TransactionType.DEPOSIT);
        recoverFailedTransactionsByType(TransactionType.WITHDRAW);
    }

    private void recoverFailedTransactionsByType(TransactionType type) {

        List<FailedTransactionModel> failedTransactions = failedTransactionRepository.findFirst100ByRecoveredIsFalseAndType(type);

        failedTransactions.forEach(model -> {

            Runnable action = () -> {

                switch (type) {
                    case TransactionType.TRANSFER ->
                            transfer(new TransferTransactionData(model.getWithdrawAccountNumber(), model.getDepositAccountNumber(), TransactionType.TRANSFER, model.getBalance()));
                    case TransactionType.DEPOSIT ->
                            deposit(new TransactionData(model.getDepositAccountNumber(), TransactionType.DEPOSIT, model.getBalance()));
                    case TransactionType.WITHDRAW ->
                            withdraw(new TransactionData(model.getWithdrawAccountNumber(), TransactionType.WITHDRAW, model.getBalance()));
                }

                model.setRecovered(true);
                failedTransactionRepository.save(model);
            };
            transactionUtil.doInTransaction(action);
        });
    }

}
