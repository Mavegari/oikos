package com.oikos.finance.debt.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DebtPaymentResponse(
    UUID id,
    UUID debtId,
    BigDecimal amount,
    LocalDate date,
    Instant createdAt
) {}