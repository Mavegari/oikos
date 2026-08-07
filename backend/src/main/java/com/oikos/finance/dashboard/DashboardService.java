package com.oikos.finance.dashboard;

import com.oikos.finance.account.Account;
import com.oikos.finance.account.AccountRepository;
import com.oikos.finance.dashboard.dto.CategorySpending;
import com.oikos.finance.dashboard.dto.DashboardResponse;
import com.oikos.finance.transaction.TransactionRepository;
import com.oikos.finance.transaction.TransactionType;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;

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
}