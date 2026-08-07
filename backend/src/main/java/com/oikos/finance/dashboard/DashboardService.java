package com.oikos.finance.dashboard;

import com.oikos.finance.account.Account;
import com.oikos.finance.account.AccountRepository;
import com.oikos.finance.dashboard.dto.CategorySpending;
import com.oikos.finance.dashboard.dto.DashboardResponse;
import com.oikos.finance.transaction.TransactionRepository;
import com.oikos.finance.transaction.TransactionType;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import com.oikos.finance.category.Category;
import com.oikos.finance.dashboard.dto.CategoryBreakdown;
import com.oikos.finance.dashboard.dto.DashboardSummary;
import com.oikos.finance.dashboard.dto.MonthlyDataPoint;
import com.oikos.finance.transaction.Transaction;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DashboardService(AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardResponse getSummary(User user, int month, int year) {
        // Rango de fechas del mes
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 1. Balance total: suma del saldo de todas las cuentas
        BigDecimal totalBalance = calculateTotalBalance(user);

        // 2. Ingresos y gastos del mes
        BigDecimal monthlyIncome = transactionRepository
                .sumAmountByUserAndTypeAndDateBetween(
                        user, TransactionType.INCOME, startDate, endDate);
        BigDecimal monthlyExpenses = transactionRepository
                .sumAmountByUserAndTypeAndDateBetween(
                        user, TransactionType.EXPENSE, startDate, endDate);
        BigDecimal monthlyBalance = monthlyIncome.subtract(monthlyExpenses);

        // 3. Gasto por categoría (convertir Object[] a CategorySpending)
        List<Object[]> rawSpending = transactionRepository
                .sumAmountByCategoryGrouped(
                        user, TransactionType.EXPENSE, startDate, endDate);

        List<CategorySpending> spendingByCategory = rawSpending.stream()
                .map(row -> new CategorySpending(
                        (String) row[0],
                        (BigDecimal) row[1]))
                .toList();

        return new DashboardResponse(
                totalBalance,
                monthlyIncome,
                monthlyExpenses,
                monthlyBalance,
                month,
                year,
                spendingByCategory
        );
    }

    // Balance total: suma del saldo de cada cuenta (ingresos - gastos de cada una)
    private BigDecimal calculateTotalBalance(User user) {
        List<Account> accounts = accountRepository.findByUser(user);
        BigDecimal total = BigDecimal.ZERO;

        for (Account account : accounts) {
            BigDecimal ingresos = transactionRepository
                    .sumAmountByAccountAndType(account, TransactionType.INCOME);
            BigDecimal gastos = transactionRepository
                    .sumAmountByAccountAndType(account, TransactionType.EXPENSE);
            total = total.add(ingresos).subtract(gastos);
        }

        return total;
    }
    /**
 * Obtiene resumen general de todas las transacciones del usuario (sin filtrar por mes)
 */
public DashboardSummary getSummary(User user) {
    List<Transaction> transactions = transactionRepository.findByUser(user);
    
    BigDecimal totalIncome = transactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    BigDecimal totalExpense = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    BigDecimal netBalance = totalIncome.subtract(totalExpense);
    
    List<CategoryBreakdown> incomeByCategory = calculateCategoryBreakdown(
            transactions, TransactionType.INCOME, totalIncome
    );
    
    List<CategoryBreakdown> expenseByCategory = calculateCategoryBreakdown(
            transactions, TransactionType.EXPENSE, totalExpense
    );
    
    return new DashboardSummary(
            totalIncome,
            totalExpense,
            netBalance,
            incomeByCategory,
            expenseByCategory
    );
}

/**
 * Obtiene serie mensual para un año específico
 */
public List<MonthlyDataPoint> getMonthlySeries(User user, Integer year) {
    List<MonthlyDataPoint> result = new ArrayList<>();
    List<Transaction> transactions = transactionRepository.findByUser(user);
    
    Map<YearMonth, List<Transaction>> byMonth = new HashMap<>();
    for (Transaction t : transactions) {
        if (t.getDate().getYear() == year) {
            YearMonth ym = YearMonth.of(t.getDate().getYear(), t.getDate().getMonth());
            byMonth.computeIfAbsent(ym, k -> new ArrayList<>()).add(t);
        }
    }
    
    for (int month = 1; month <= 12; month++) {
        YearMonth ym = YearMonth.of(year, month);
        List<Transaction> monthTransactions = byMonth.getOrDefault(ym, Collections.emptyList());
        
        BigDecimal income = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal expense = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal net = income.subtract(expense);
        result.add(new MonthlyDataPoint(month, year, income, expense, net));
    }
    
    return result;
}

/**
 * Calcula desglose por categoría con porcentajes
 */
private List<CategoryBreakdown> calculateCategoryBreakdown(
        List<Transaction> transactions, TransactionType type, BigDecimal total) {
    
    Map<Category, BigDecimal> byCategory = new HashMap<>();
    
    for (Transaction t : transactions) {
        if (t.getType() == type) {
            byCategory.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
        }
    }
    
    List<CategoryBreakdown> result = new ArrayList<>();
    
    if (total.compareTo(BigDecimal.ZERO) > 0) {
        for (Map.Entry<Category, BigDecimal> entry : byCategory.entrySet()) {
            Double percentage = entry.getValue()
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .doubleValue();
            
            result.add(new CategoryBreakdown(
                    entry.getKey().getId(),
                    entry.getKey().getName(),
                    entry.getValue(),
                    percentage
            ));
        }
    }
    
    result.sort((a, b) -> b.amount().compareTo(a.amount()));
    return result;
        }
}