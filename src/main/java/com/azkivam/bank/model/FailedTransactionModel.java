package com.azkivam.bank.model;

import com.azkivam.bank.baseinfo.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "FAILED_TX")
@SequenceGenerator(name = "FAILED_TX_SEQ", initialValue = 1000, allocationSize = 1)
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class FailedTransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAILED_TX_SEQ")
    private Long id;

    private TransactionType type;

    @Column(name = "DEPOSIT_NUMBER")
    private String depositAccountNumber;

    @Column(name = "WITHDRAW_NUMBER")
    private String withdrawAccountNumber;

    private double balance;

    private boolean recovered;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailedTransactionModel that = (FailedTransactionModel) o;
        return recovered == that.recovered && Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(depositAccountNumber, that.depositAccountNumber) && Objects.equals(withdrawAccountNumber, that.withdrawAccountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, depositAccountNumber, withdrawAccountNumber, recovered);
    }
}
