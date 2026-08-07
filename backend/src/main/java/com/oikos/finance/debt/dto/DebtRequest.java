package com.oikos.finance.debt.dto;

import com.oikos.finance.debt.DebtType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DebtRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String name,
    
    @NotNull(message = "El importe total es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
    BigDecimal totalAmount,
    
    @NotNull(message = "El tipo de deuda es obligatorio")
    DebtType type
) {}