package com.oikos.finance.investment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record InvestmentValuationResponse(
    UUID id,
    UUID investmentId,
    BigDecimal value,
    LocalDate date,
    Instant createdAt
) {}