package com.oikos.finance.investment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvestmentResponse(
    UUID id,
    String name,
    BigDecimal investedAmount,
    BigDecimal currentValue,
    BigDecimal profitLoss,
    Double profitLossPercentage,
    List<InvestmentValuationDTO> valuations,
    Instant createdAt
) {}