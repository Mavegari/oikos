package com.oikos.finance.investment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentValuationRequest(
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    BigDecimal value,
    
    @NotNull(message = "La fecha es obligatoria")
    LocalDate date
) {}