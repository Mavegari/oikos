package com.oikos.finance.account.dto;

import com.oikos.finance.account.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        AccountType type,
        BigDecimal initialBalance,
        BigDecimal balance
) {
}