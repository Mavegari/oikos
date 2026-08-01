package com.oikos.finance.budget.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        BigDecimal limitAmount,
        BigDecimal spent,
        BigDecimal remaining,
        int month,
        int year,
        boolean exceeded
) {
}