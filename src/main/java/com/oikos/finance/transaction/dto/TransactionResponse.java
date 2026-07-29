package com.oikos.finance.transaction.dto;

import com.oikos.finance.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        String accountName,
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        TransactionType type,
        LocalDate date,
        String note
) {
}