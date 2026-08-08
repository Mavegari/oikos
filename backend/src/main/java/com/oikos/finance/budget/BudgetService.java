package com.oikos.finance.budget;

import com.oikos.finance.budget.dto.BudgetRequest;
import com.oikos.finance.budget.dto.BudgetResponse;
import com.oikos.finance.category.Category;
import com.oikos.finance.category.CategoryRepository;
import com.oikos.finance.transaction.TransactionRepository;
import com.oikos.finance.transaction.TransactionType;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    // CREAR
    public BudgetResponse create(BudgetRequest request, User user) {
        // Verificar que la categoría es del usuario
        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Evitar duplicado (mismo usuario+categoría+mes+año)
        if (budgetRepository.existsByUserAndCategoryAndMonthAndYear(
                user, category, request.month(), request.year())) {
            throw new IllegalArgumentException(
                    "Ya tienes un presupuesto para esta categoría en ese mes");
        }

       Budget budget = new Budget(
                user, category, request.limitAmount(),
                request.month(), request.year(),
                request.recurring() != null ? request.recurring() : false);
        Budget saved = budgetRepository.save(budget);
        return toResponse(saved);
    }

    // LISTAR (todos, o filtrados por mes/año si se indican)
    public List<BudgetResponse> findAll(User user, Integer month, Integer year) {
        List<Budget> budgets = (month != null && year != null)
                ? budgetRepository.findByUserAndMonthAndYear(user, month, year)
                : budgetRepository.findByUser(user);
        return budgets.stream().map(this::toResponse).toList();
    }

    // OBTENER uno
    public BudgetResponse findById(UUID id, User user) {
        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado"));
        return toResponse(budget);
    }

    // ACTUALIZAR
    
    public BudgetResponse update(UUID id, BudgetRequest request, User user) {
        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado"));

        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        budget.setCategory(category);
        budget.setLimitAmount(request.limitAmount());
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setRecurring(request.recurring() != null ? request.recurring() : false);

        Budget updated = budgetRepository.save(budget);
        return toResponse(updated);
    }

    // ELIMINAR
    public void delete(UUID id, User user) {
        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado"));
        budgetRepository.delete(budget);
    }

    // Conversión entidad → DTO, con cálculo de gastado/restante/superado
    private BudgetResponse toResponse(Budget budget) {
        BigDecimal spent = calculateSpent(budget);
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        boolean exceeded = spent.compareTo(budget.getLimitAmount()) > 0;

       return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getLimitAmount(),
                spent,
                remaining,
                budget.getMonth(),
                budget.getYear(),
                budget.getRecurring(),
                exceeded
        );
    }

    // Calcula cuánto se ha gastado en esa categoría ese mes
    private BigDecimal calculateSpent(Budget budget) {
        LocalDate startDate = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return transactionRepository.sumAmountByCategoryAndTypeAndDateBetween(
                budget.getCategory(),
                TransactionType.EXPENSE,
                startDate,
                endDate);
    }
}