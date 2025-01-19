package com.azkivam.bank.dto;

import jakarta.validation.constraints.NotBlank;

public record User(Long id,
                   @NotBlank String username,
                   String password,
                   @NotBlank String firstname,
                   @NotBlank String lastname,
                   int version) {

    public String getName() {
        return firstname + " " + lastname;
    }

}
