package com.oikos.finance.recurring.dto;

import com.oikos.finance.recurring.DayOfWeek;
import com.oikos.finance.recurring.Frequency;
import com.oikos.finance.recurring.RecurrenceMode;
import com.oikos.finance.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RecurringTransactionResponse(
    UUID id,
    UUID accountId,
    String accountName,
    UUID categoryId,
    String categoryName,
    BigDecimal amount,
    TransactionType type,
    RecurrenceMode recurrenceMode,
    
    // Solo para PATTERN mode
    Frequency frequency,
    Integer interval,
    Integer dayOfMonth,
    DayOfWeek dayOfWeek,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate nextRunDate,
    
    // Solo para MANUAL mode
    List<ScheduledDateDTO> scheduledDates,
    Integer pendingPaymentsCount,
    
    // Común
    Boolean active,
    String description,
    Long daysUntilNextRun,
    Instant createdAt
) {}