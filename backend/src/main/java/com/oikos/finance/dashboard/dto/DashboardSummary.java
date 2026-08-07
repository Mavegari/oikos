package com.oikos.finance.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance,
    List<CategoryBreakdown> incomeByCategory,
    List<CategoryBreakdown> expenseByCategory
) {}
