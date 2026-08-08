package com.oikos.finance.budget;

import com.oikos.finance.category.Category;
import com.oikos.finance.category.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class RecurringBudgetScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(RecurringBudgetScheduler.class);
    
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    
    public RecurringBudgetScheduler(BudgetRepository budgetRepository,
                                    CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Clona presupuestos recurrentes el primer día del mes a las 3 AM
     */
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void cloneRecurringBudgets() {
        logger.info("Iniciando clonación de presupuestos recurrentes");
        
        try {
            YearMonth nextMonth = YearMonth.now().plusMonths(1);
            int month = nextMonth.getMonthValue();
            int year = nextMonth.getYear();
            
            // Obtener todos los presupuestos recurrentes
            List<Budget> recurringBudgets = budgetRepository.findByRecurringTrue();
            
            for (Budget originalBudget : recurringBudgets) {
                try {
                    cloneBudget(originalBudget, month, year);
                } catch (Exception e) {
                    logger.error("Error clonando presupuesto recurrente " + originalBudget.getId(), e);
                }
            }
            
            logger.info("Clonación de presupuestos recurrentes completada");
        } catch (Exception e) {
            logger.error("Error en scheduler de presupuestos recurrentes", e);
        }
    }
    
    /**
     * Clona un presupuesto para el próximo mes
     */
    private void cloneBudget(Budget original, int month, int year) {
        // Verificar que no exista presupuesto para ese mes+año
        if (budgetRepository.existsByUserAndCategoryAndMonthAndYear(
                original.getUser(), original.getCategory(), month, year)) {
            logger.info("Presupuesto para " + original.getCategory().getName() + " en " + month + "/" + year + " ya existe, saltando");
            return;
        }
        
        // Crear nuevo presupuesto
        Budget newBudget = new Budget(
                original.getUser(),
                original.getCategory(),
                original.getLimitAmount(),
                month,
                year,
                true  // recurring = true
        );
        
        budgetRepository.save(newBudget);
        logger.info("Presupuesto clonado para " + original.getCategory().getName() + " en " + month + "/" + year);
    }
}