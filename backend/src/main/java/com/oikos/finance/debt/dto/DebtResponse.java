package com.oikos.finance.debt.dto;

import com.oikos.finance.debt.DebtType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DebtResponse(
    UUID id,
    String name,
    BigDecimal totalAmount,
    BigDecimal paid,
    BigDecimal pending,
    Double pendingPercentage,
    DebtType type,
    List<DebtPaymentDTO> payments,
    Instant createdAt
) {}

record DebtPaymentDTO(
    UUID id,
    BigDecimal amount,
    String date
) {}