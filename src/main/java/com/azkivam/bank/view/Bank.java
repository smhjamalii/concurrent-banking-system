package com.azkivam.bank.view;

import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.dto.User;
import com.azkivam.bank.exception.BusinessException;
import com.azkivam.bank.service.bankaccount.BankAccountService;
import com.azkivam.bank.service.bankaccount.command.CreateBankAccountCommand;
import com.azkivam.bank.service.bankaccount.command.FindBankAccountCommand;
import com.azkivam.bank.service.transaction.TransactionService;
import com.azkivam.bank.service.transaction.command.DepositCommand;
import com.azkivam.bank.service.transaction.command.TransferCommand;
import com.azkivam.bank.service.transaction.command.WithdrawCommand;
import com.azkivam.bank.service.user.UserService;
import com.azkivam.bank.service.user.command.CreateNewUserCommand;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
@RequiredArgsConstructor @Slf4j
@Profile("production")
public class Bank implements CommandLineRunner {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    @Override
    public void run(String... args) throws Exception {

        buildMenu();
    }

    private void buildMenu(){
        String menu = """
                
                [1] Signup
                [2] Create Bank Account
                [3] Deposit
                [4] Withdraw
                [5] Transfer
                [6] Account Balance
                [7] Exit
                Select one item please: """;

        try(Scanner in = new Scanner(System.in);) {
            System.out.print(menu);
            String userInput = null;
            while (!(userInput = in.nextLine()).trim().equals("7")) {
                switch (userInput) {
                    case "1" -> signup(in);
                    case "2" -> createBankAccount(in);
                    case "3" -> deposit(in);
                    case "4" -> withdraw(in);
                    case "5" -> transfer(in);
                    case "6" -> balance(in);
                }

                System.out.println(menu);
            }
        }
    }

    private void signup(Scanner in){
        System.out.println("""
                Enter comma-separated user information (username, password, firstname, lastname) :
                e.g. : test, test, test, test
                """);
        String[] userInput = in.nextLine().split(",");
        try {

            validateUserInput(userInput, 4);

            User user = new User(null, userInput[0].trim(), userInput[1].trim(), userInput[2].trim(), userInput[3].trim(), 0);
            CreateNewUserCommand newUserCommand = new CreateNewUserCommand(userService, user);
            newUserCommand.execute();

            log.info("New user \"{}\" has been created. You can use it as a bank account holder.", user.username());

        } catch (ConstraintViolationException | DataIntegrityViolationException e) {

            log.error("User already exist!");

        } catch (BusinessException e) {

            log.error(e.getMessage());
        }
    }

    private void createBankAccount(Scanner in){
        System.out.println("""
                Enter comma-separated bank account information (holder's username, initial balance) :
                e.g. : test, 1000
                """);
        String[] userInput = in.nextLine().split(",");
        try {

            validateUserInput(userInput, 2);

            CreateBankAccountCommand createBankAccountCommand = new CreateBankAccountCommand(
                    bankAccountService, userService, transactionService,
                    userInput[0].trim(), Double.parseDouble(userInput[1].trim())
                    );

            BankAccount bankAccount = createBankAccountCommand.execute();

            log.info("Bank account has been created, holder: {}, number: {}, balance: {}",
                    bankAccount.holder().getName(), bankAccount.number(), bankAccount.balance());

        } catch (BusinessException e) {

            log.error(e.getMessage());
        }
    }

    private void deposit(Scanner in) {
        System.out.println("""
                Enter comma-separated bank account number and an amount to deposit:
                e.g. : 4d6cb373-e407-4002-9588-6579bc501997, 1000
                """);
        String[] userInput = in.nextLine().split(",");
        try {

            validateUserInput(userInput, 2);

            DepositCommand depositCommand = new DepositCommand(transactionService, userInput[0].trim(), Double.parseDouble(userInput[1].trim()));
            depositCommand.execute();

            FindBankAccountCommand findBankAccountCommand = new FindBankAccountCommand(bankAccountService, depositCommand.getNumber());
            Optional<BankAccount> bankAccount = findBankAccountCommand.execute();
            bankAccount.ifPresentOrElse(
                    b -> log.info("{} was deposited to {}, current balance: {}", depositCommand.getBalance(), depositCommand.getNumber(), b.balance()),
                    () -> log.info("Bank account not found or was deleted!")
            );

        } catch (BusinessException e) {

            log.error(e.getMessage());
        }
    }

    private void withdraw(Scanner in) {
        System.out.println("""
                Enter comma-separated bank account number and an amount to withdraw:
                e.g. : 4d6cb373-e407-4002-9588-6579bc501997, 1000
                """);
        String[] userInput = in.nextLine().split(",");
        try {

            validateUserInput(userInput, 2);

            WithdrawCommand withdrawCommand = new WithdrawCommand(transactionService, userInput[0].trim(), Double.parseDouble(userInput[1].trim()));
            withdrawCommand.execute();

            FindBankAccountCommand findBankAccountCommand = new FindBankAccountCommand(bankAccountService, withdrawCommand.getNumber());
            Optional<BankAccount> bankAccount = findBankAccountCommand.execute();
            bankAccount.ifPresentOrElse(
                    b -> log.info("{} was withdrew from {}, Remaining balance: {}", withdrawCommand.getBalance(), withdrawCommand.getNumber(), b.balance()),
                    () -> log.info("Bank account not found or was deleted!")
            );

        } catch (BusinessException e) {

            log.error(e.getMessage());
        }
    }

    private void transfer(Scanner in) {
        System.out.println("""
                Enter comma-separated origin bank account number, destination bank account number and an amount to transfer:
                e.g. : 4d6cb373-e407-4002-9588-6579bc501997, 4d6cb373-e407-4002-9588-6579bc501998, 1000
                """);
        String[] userInput = in.nextLine().split(",");
        try {
            validateUserInput(userInput, 3);

            TransferCommand transferCommand = new TransferCommand(transactionService, userInput[0].trim(), userInput[1].trim(), Double.parseDouble(userInput[2].trim()));
            transferCommand.execute();

            log.info("{} transferred from {} to {}", transferCommand.getBalance(), transferCommand.getFromNumber(), transferCommand.getToNumber());

        } catch (BusinessException e) {

            log.error(e.getMessage());
        }
    }

    private void balance(Scanner in) {
        System.out.println("""
                Enter bank account number:
                e.g. : 4d6cb373-e407-4002-9588-6579bc501997
                """);
        String bankAccountNumber = in.nextLine();

        FindBankAccountCommand findBankAccountCommand = new FindBankAccountCommand(bankAccountService, bankAccountNumber);
        Optional<BankAccount> bankAccount = findBankAccountCommand.execute();

        bankAccount.ifPresentOrElse(
                b -> log.info("Balance: {}", b.balance()),
                () -> log.error("Bank account not found!")
        );
    }

    private void validateUserInput(String[] userInput, int validElementCount) {
        if (userInput.length < validElementCount) {
            throw new BusinessException("Input is invalid!");
        }
    }

}
