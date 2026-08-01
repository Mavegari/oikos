package com.oikos.finance.transaction.dto;

import com.oikos.finance.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(

        @NotNull(message = "La cuenta es obligatoria")
        UUID accountId,

        @NotNull(message = "La categoría es obligatoria")
        UUID categoryId,

        @NotNull(message = "El importe es obligatorio")
        @Positive(message = "El importe debe ser mayor que cero")
        BigDecimal amount,

        @NotNull(message = "El tipo es obligatorio (INCOME o EXPENSE)")
        TransactionType type,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate date,

        String note
) {
}