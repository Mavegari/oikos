package com.oikos.finance.dashboard.dto;

import java.math.BigDecimal;

public record CategorySpending(
        String categoryName,
        BigDecimal total
) {
}