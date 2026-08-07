package com.oikos.finance.account.dto;

import com.oikos.finance.account.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AccountRequest(
    @NotBlank(message = "El nombre de la cuenta es obligatorio")
    String name,
    
    @NotNull(message = "El tipo de cuenta es obligatorio")
    AccountType type,
    
    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial no puede ser negativo")
    BigDecimal initialBalance
) {}