package com.oikos.finance.budget.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetRequest(

        @NotNull(message = "La categoría es obligatoria")
        UUID categoryId,

        @NotNull(message = "El límite es obligatorio")
        @Positive(message = "El límite debe ser mayor que cero")
        BigDecimal limitAmount,

        @NotNull(message = "El mes es obligatorio")
        @Min(value = 1, message = "El mes debe estar entre 1 y 12")
        @Max(value = 12, message = "El mes debe estar entre 1 y 12")
        Integer month,

        @NotNull(message = "El año es obligatorio")
        @Min(value = 2000, message = "El año no es válido")
        Integer year
) {
}