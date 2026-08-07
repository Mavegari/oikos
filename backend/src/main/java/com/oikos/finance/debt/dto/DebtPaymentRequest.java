package com.oikos.finance.debt.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtPaymentRequest(
    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
    BigDecimal amount,
    
    @NotNull(message = "La fecha es obligatoria")
    LocalDate date
) {}