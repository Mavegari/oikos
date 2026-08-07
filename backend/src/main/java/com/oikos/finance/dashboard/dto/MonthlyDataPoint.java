package com.oikos.finance.dashboard.dto;

import java.math.BigDecimal;

public record MonthlyDataPoint(
    Integer month,
    Integer year,
    BigDecimal income,
    BigDecimal expense,
    BigDecimal net
) {}