package com.oikos.finance.transfer.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransferResponse(
    UUID id,
    UUID sourceAccountId,
    String sourceAccountName,
    UUID targetAccountId,
    String targetAccountName,
    BigDecimal amount,
    LocalDate date,
    String note,
    Instant createdAt
) {}