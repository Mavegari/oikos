package com.oikos.finance.account.dto;

import com.oikos.finance.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AccountRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "El tipo es obligatorio (CASH, BANK o CARD)")
        AccountType type,

        @NotNull(message = "El saldo inicial es obligatorio")
        BigDecimal initialBalance
) {
}