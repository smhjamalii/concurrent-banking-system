package com.azkivam.bank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "USERS")
@SequenceGenerator(name = "USERS_SEQ", initialValue = 1000, allocationSize = 1)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ")
    private Long id;

    @Column(name = "USERNAME", unique = true)
    private String username;

    private String password;

    private String firstname;

    private String lastname;

    @Version
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(username, userModel.username) && Objects.equals(password, userModel.password)
                && Objects.equals(firstname, userModel.firstname) && Objects.equals(lastname, userModel.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, firstname, lastname);
    }
}
