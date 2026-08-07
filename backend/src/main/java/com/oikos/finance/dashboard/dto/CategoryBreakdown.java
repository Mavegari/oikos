package com.oikos.finance.dashboard.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryBreakdown(
    UUID categoryId,
    String categoryName,
    BigDecimal amount,
    Double percentage
) {}