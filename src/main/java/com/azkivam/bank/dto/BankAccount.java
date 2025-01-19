package com.azkivam.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BankAccount(Long id,
                          @NotBlank String number,
                          @NotNull User holder,
                          double balance,
                          int version) {
}
