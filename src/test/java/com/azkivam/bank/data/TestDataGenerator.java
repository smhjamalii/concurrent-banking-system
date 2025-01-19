package com.azkivam.bank.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Objects;
import java.util.UUID;

public class TestDataGenerator {

    public static void main(String[] args) {

        new TestDataGenerator().generatedData();
    }

    private void generatedData(){
        String userInsertsPath = Objects.requireNonNull(TestDataGenerator.class.getClassLoader().getResource("sql/user-inserts.sql")).getPath();

        System.out.printf("Pick up the user insert file form here: %s%n", userInsertsPath);

        var insertUser = "insert into users (username, password, firstname, lastname, version) values ('user_%d', 'user', 'USER', '%d', 0);%n";

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userInsertsPath))) {
            var central_bank_insert = "insert into users (username, password, firstname, lastname, version) values ('central_bank', 'user', 'Central Bank', 'gov', 0);";
            bufferedWriter.write(central_bank_insert);
            for (int i = 0; i < 1000; i++) {
                bufferedWriter.write(insertUser.formatted(i,i));
            }
        } catch (Exception e){

            e.printStackTrace();
        }

        String bankAccountInsertsPath = Objects.requireNonNull(TestDataGenerator.class.getClassLoader().getResource("sql/bankaccount-inserts.sql")).getPath();

        System.out.printf("Pick up the bank account insert file form here: %s%n", bankAccountInsertsPath);

        var insertBankAccount = "insert into BANK_ACCOUNTS (ACCOUNT_NUMBER, USER_ID, balance) VALUES ('%s', %d, %d);%n";

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(bankAccountInsertsPath))) {
            var insertCentralBankAccount = "insert into BANK_ACCOUNTS (ACCOUNT_NUMBER, USER_ID, balance) VALUES ('%s', 1000, 100000000000);%n";
            bufferedWriter.write(insertCentralBankAccount.formatted(UUID.randomUUID()));
            for (int i = 1001; i < 2001; i++) {
                bufferedWriter.write(insertBankAccount.formatted(UUID.randomUUID(), i, 1));
            }
        } catch (Exception e){

            e.printStackTrace();
        }
    }

}
