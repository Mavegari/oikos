package com.oikos.finance.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal totalBalance,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpenses,
        BigDecimal monthlyBalance,
        int month,
        int year,
        List<CategorySpending> spendingByCategory
) {
}