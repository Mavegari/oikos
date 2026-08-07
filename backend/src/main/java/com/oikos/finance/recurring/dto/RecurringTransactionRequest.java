package com.oikos.finance.recurring.dto;

import com.oikos.finance.recurring.DayOfWeek;
import com.oikos.finance.recurring.Frequency;
import com.oikos.finance.recurring.RecurrenceMode;
import com.oikos.finance.transaction.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RecurringTransactionRequest(
    @NotNull(message = "Cuenta es obligatoria")
    UUID accountId,
    
    @NotNull(message = "Categoría es obligatoria")
    UUID categoryId,
    
    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
    BigDecimal amount,
    
    @NotNull(message = "El tipo es obligatorio")
    TransactionType type, // INCOME, EXPENSE
    
    @NotNull(message = "El modo de recurrencia es obligatorio")
    RecurrenceMode recurrenceMode, // PATTERN, MANUAL
    
    // Solo para PATTERN mode
    Frequency frequency,
    @Positive(message = "El intervalo debe ser mayor a 0")
    Integer interval,
    @Min(1) @Max(31)
    Integer dayOfMonth,
    DayOfWeek dayOfWeek,
    LocalDate startDate,
    LocalDate endDate,
    
    // Solo para MANUAL mode
    @Size(min = 1, message = "Al menos una fecha es obligatoria para modo MANUAL")
    List<LocalDate> scheduledDates
) {}