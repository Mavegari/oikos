package com.oikos.finance.investment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InvestmentRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String name,
    
    @NotNull(message = "El importe invertido es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
    BigDecimal investedAmount
) {}