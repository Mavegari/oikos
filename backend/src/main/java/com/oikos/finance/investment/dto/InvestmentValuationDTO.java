package com.oikos.finance.investment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InvestmentValuationDTO(
    UUID id,
    BigDecimal value,
    String date
) {}