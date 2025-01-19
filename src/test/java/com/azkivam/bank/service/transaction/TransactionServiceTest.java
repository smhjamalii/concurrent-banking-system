package com.azkivam.bank.service.transaction;

import com.azkivam.bank.baseinfo.TransactionType;
import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.model.FailedTransactionModel;
import com.azkivam.bank.repository.FailedTransactionRepository;
import com.azkivam.bank.service.bankaccount.BankAccountService;
import com.azkivam.bank.service.bankaccount.command.FindBankAccountByUsernameCommand;
import com.azkivam.bank.service.transaction.command.DepositCommand;
import com.azkivam.bank.service.transaction.command.TransferCommand;
import com.azkivam.bank.service.transaction.command.WithdrawCommand;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

@SpringBootTest
@Slf4j
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private FailedTransactionRepository failedTransactionRepository;

    /**
     * Unit test for concurrent transfer transactions
     */
    @Test
    void shouldExecuteTransferTransactionsConcurrently() {

        /** GIVEN **/
        BankAccount centralBank = new FindBankAccountByUsernameCommand(bankAccountService, "central_bank").execute().get();

        double gift = 100_000_000D;

        // central bank distribution
        BiConsumer<Integer, Integer> transferTask = (start, end) -> {
            for (int i = start; i < end; i++) {
                try {
                    FindBankAccountByUsernameCommand findBankAccountByUsernameCommand = new FindBankAccountByUsernameCommand(bankAccountService, "user_" + i);
                    BankAccount bankAccount = findBankAccountByUsernameCommand.execute().get();
                    TransferCommand transferCommand = new TransferCommand(transactionService, centralBank.number(), bankAccount.number(), gift);
                    transferCommand.execute();
                } catch (Exception e) {

                    log.error(e.getMessage());
                }
            }
        };

        Runnable distributeTaskPart1 = () -> transferTask.accept(0, 250);
        Runnable distributeTaskPart2 = () -> transferTask.accept(250, 500);

        double tax = 1D;

        // tax payers payback
        BiConsumer<Integer, Integer> taxTask = (start, end) -> {
            for (int i = start; i < end; i++) {
                try {
                    FindBankAccountByUsernameCommand findBankAccountByUsernameCommand = new FindBankAccountByUsernameCommand(bankAccountService, "user_" + i);
                    BankAccount bankAccount = findBankAccountByUsernameCommand.execute().get();
                    TransferCommand transferCommand = new TransferCommand(transactionService, bankAccount.number(), centralBank.number(), tax);
                    transferCommand.execute();
                } catch (Exception e) {

                    log.error(e.getMessage());
                }
            }
        };

        Runnable taxTaskPart1 = () -> taxTask.accept(0, 250);
        Runnable taxTaskPart2 = () -> taxTask.accept(250, 500);

        final double central_bank_balance = 100_000_000_000D;
        final double remainingBankAccountBalance = central_bank_balance - (gift * 500) + (tax * 500);

        /** WHEN **/
        try (ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);) {
            executorService.submit(distributeTaskPart1);
            executorService.submit(distributeTaskPart2);
            executorService.submit(taxTaskPart1);
            executorService.submit(taxTaskPart2);
        }

        /** THEN **/

        waitUntilAllFailedTransactionsAreRecovered();

        BankAccount cb = new FindBankAccountByUsernameCommand(bankAccountService, "central_bank").execute().get();

        log.info("Central Bank balance: {}", ((long) cb.balance()));
        log.info("Must be: {}", ((long) remainingBankAccountBalance));

        Assertions.assertThat(cb.balance()).isEqualTo(remainingBankAccountBalance);

    }

    private void waitUntilAllFailedTransactionsAreRecovered() {
        List<FailedTransactionModel> list;
        do {
            list = failedTransactionRepository.findFirst100ByRecoveredIsFalseAndType(TransactionType.TRANSFER);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        } while (!list.isEmpty());
    }

    @Test
    void shouldExecuteDepositTransactionsConcurrently(){

        /** GIVEN **/
        BankAccount centralBank = new FindBankAccountByUsernameCommand(bankAccountService, "central_bank").execute().get();

        double tax = 1D;

        // tax payers payback
        BiConsumer<Integer, Integer> taxTask = (start, end) -> {
            for (int i = start; i < end; i++) {
                DepositCommand depositCommand = new DepositCommand(transactionService, centralBank.number(), tax);
                depositCommand.execute();
            }
        };

        Runnable taxTaskPart1 = () -> taxTask.accept(0, 250);
        Runnable taxTaskPart2 = () -> taxTask.accept(250, 500);
        Runnable taxTaskPart3 = () -> taxTask.accept(500, 750);
        Runnable taxTaskPart4 = () -> taxTask.accept(750, 1000);

        final double central_bank_balance = 100_000_000_000D;
        final double remainingBankAccountBalance = central_bank_balance + (tax * 1000);

        /** WHEN **/
        try (ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);) {
            executorService.submit(taxTaskPart1);
            executorService.submit(taxTaskPart2);
            executorService.submit(taxTaskPart3);
            executorService.submit(taxTaskPart4);
        }

        /** THEN **/

        waitUntilAllFailedDepositTransactionsAreRecovered();

        BankAccount cb = new FindBankAccountByUsernameCommand(bankAccountService, "central_bank").execute().get();

        log.info("Central Bank balance: {}", ((long) cb.balance()));
        log.info("Must be: {}", ((long) remainingBankAccountBalance));

        Assertions.assertThat(cb.balance()).isEqualTo(remainingBankAccountBalance);
    }

    private void waitUntilAllFailedDepositTransactionsAreRecovered() {
        List<FailedTransactionModel> list;
        do {
            list = failedTransactionRepository.findFirst100ByRecoveredIsFalseAndType(TransactionType.DEPOSIT);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        } while (!list.isEmpty());
    }

    @Test
    void shouldExecuteWithdrawTransactionsConcurrently(){

        /** GIVEN **/
        BankAccount centralBank = new FindBankAccountByUsernameCommand(bankAccountService, "central_bank").execute().get();

        double gift = 100_000_000D;

        // tax payers payback
        BiConsumer<Integer, Integer> giftTask = (start, end) -> {
            for (int i = start; i < end; i++) {
                WithdrawCommand withdrawCommand = new WithdrawCommand(transactionService, centralBank.number(), gift);
                withdrawCommand.execute();
            }
        };

        Runnable giftTaskPart1 = () -> giftTask.accept(0, 250);
        Runnable giftTaskPart2 = () -> giftTask.accept(250, 500);
        Runnable giftTaskPart3 = () -> giftTask.accept(500, 750);
        Runnable giftTaskPart4 = () -> giftTask.accept(750, 1000);

        final double central_bank_balance = 100_000_000_000D;
        final double remainingBankAccountBalance = central_bank_balance - (gift * 1000);

        /** WHEN **/
        try (ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);) {
            executorService.submit(giftTaskPart1);
            executorService.submit(giftTaskPart2);
            executorService.submit(giftTaskPart3);
            executorService.submit(giftTaskPart4);
        }

        /** THEN **/

        waitUntilAllFailedWithdrawTransactionsAreRecovered();

        BankAccount cb = new FindBankAccountByUsernameCommand(bankAccountService, "central_bank").execute().get();

        log.info("Central Bank balance: {}", ((long) cb.balance()));
        log.info("Must be: {}", ((long) remainingBankAccountBalance));

        Assertions.assertThat(cb.balance()).isEqualTo(remainingBankAccountBalance);
    }

    private void waitUntilAllFailedWithdrawTransactionsAreRecovered() {
        List<FailedTransactionModel> list;
        do {
            list = failedTransactionRepository.findFirst100ByRecoveredIsFalseAndType(TransactionType.WITHDRAW);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        } while (!list.isEmpty());
    }

}
