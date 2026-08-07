package com.oikos.finance.transfer.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferRequest(
    @NotNull(message = "Cuenta origen es obligatoria")
    UUID sourceAccountId,
    
    @NotNull(message = "Cuenta destino es obligatoria")
    UUID targetAccountId,
    
    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
    BigDecimal amount,
    
    @NotNull(message = "La fecha es obligatoria")
    LocalDate date,
    
    String note
) {}