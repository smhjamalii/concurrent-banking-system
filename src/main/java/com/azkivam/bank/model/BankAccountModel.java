package com.azkivam.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "BANK_ACCOUNTS")
@SequenceGenerator(name = "BANK_ACCOUNTS_SEQ", initialValue = 1000, allocationSize = 1)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccountModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANK_ACCOUNTS_SEQ")
	private Long id;	
	
	@Column(name = "ACCOUNT_NUMBER", unique = true)
	private String number;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private UserModel holder;
	
	private double balance;

	@Version
	private int version;

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankAccountModel other = (BankAccountModel) obj;
		return Objects.equals(number, other.number);
	}
		
}
