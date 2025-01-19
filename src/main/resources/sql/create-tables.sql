CREATE SEQUENCE USERS_SEQ START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE BANK_ACCOUNTS_SEQ START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE FAILED_TX_SEQ START WITH 1000 INCREMENT BY 1;

create table USERS (
    ID int DEFAULT NEXT VALUE FOR USERS_SEQ PRIMARY KEY,
    username varchar(100) not null,
    password varchar(100),
    firstname varchar(100) not null,
    lastname varchar(100) not null,
    version int not null default 0,
    unique (username)
);

create table BANK_ACCOUNTS (
    ID int DEFAULT NEXT VALUE FOR BANK_ACCOUNTS_SEQ primary key,
    ACCOUNT_NUMBER varchar(100) not null,
    USER_ID int not null,
    balance double,
    version int not null default 0,
    FOREIGN KEY (USER_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
    UNIQUE (ACCOUNT_NUMBER)
);

create table FAILED_TX (
    ID int DEFAULT NEXT VALUE FOR FAILED_TX_SEQ primary key,
    type varchar(50),
    DEPOSIT_NUMBER varchar(100),
    WITHDRAW_NUMBER varchar(100),
    balance double,
    recovered bit
);