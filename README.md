# Concurrent Banking System

### How to run the project

There is a class called Bank in the main.java.com.azkivam.bank.view package which is responsible for creating a menu in console. 
For checking the functionalities of the project the Bank class is useful. But it doesn't do anything concurrent. There is a 
unit test under test.java.com.azkivam.bank.service.transaction which simulates a concurrent environment with 4 tasks.

### Transfer transaction test scenario

A bank account which has a massive balance (central bank) begins to distribute its money among 500 bank accounts. It gives
100_000_000 IRR to every account as a gift. At the same time those 500 accounts pay back 1 IRR as their tax to central bank.  

### Test environment preparation

Before running tests it is necessary to execute TestDataGenerator.main for generating. It will create two files under
target/test-classes/sql directly namely bankaccount-inserts.sql and user-inserts.sql. Copy these two files into resources/sql
package if they aren't exist there.
