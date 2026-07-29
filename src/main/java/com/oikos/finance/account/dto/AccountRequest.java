package com.oikos.finance.account.dto;

import com.oikos.finance.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "El tipo es obligatorio (CASH, BANK o CARD)")
        AccountType type
) {
}