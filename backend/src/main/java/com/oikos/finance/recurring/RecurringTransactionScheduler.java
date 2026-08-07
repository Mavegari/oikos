package com.oikos.finance.recurring;

import com.oikos.finance.transaction.Transaction;
import com.oikos.finance.transaction.TransactionRepository;
import com.oikos.finance.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Component
public class RecurringTransactionScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(RecurringTransactionScheduler.class);
    
    private final RecurringTransactionRepository recurringRepository;
    private final RecurringTransactionDateRepository dateRepository;
    private final RecurringTransactionService recurringService;
    private final TransactionRepository transactionRepository;
    
    public RecurringTransactionScheduler(RecurringTransactionRepository recurringRepository,
                                         RecurringTransactionDateRepository dateRepository,
                                         RecurringTransactionService recurringService,
                                         TransactionRepository transactionRepository) {
        this.recurringRepository = recurringRepository;
        this.dateRepository = dateRepository;
        this.recurringService = recurringService;
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * Ejecuta transacciones recurrentes a las 2 AM diariamente
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void executeRecurringTransactions() {
        logger.info("Iniciando ejecución de transacciones recurrentes");
        LocalDate today = LocalDate.now();
        
        try {
            // ========== MODO PATTERN ==========
            List<RecurringTransaction> dueRecurrings = recurringRepository
                    .findAllActivePatternByNextRunDateLessThanEqual(null, today);
            
            for (RecurringTransaction recurring : dueRecurrings) {
                try {
                    executePatternRecurring(recurring, today);
                } catch (Exception e) {
                    logger.error("Error ejecutando transacción recurrente PATTERN " + recurring.getId(), e);
                }
            }
            
            // ========== MODO MANUAL ==========
            List<RecurringTransactionDate> dueDates = dateRepository
                    .findAllByScheduledDateLessThanEqualAndExecutedFalse(today);
            
            for (RecurringTransactionDate rtDate : dueDates) {
                try {
                    executeManualRecurring(rtDate, today);
                } catch (Exception e) {
                    logger.error("Error ejecutando transacción recurrente MANUAL " + rtDate.getId(), e);
                }
            }
            
            logger.info("Ejecución de transacciones recurrentes completada");
        } catch (Exception e) {
            logger.error("Error en scheduler de transacciones recurrentes", e);
        }
    }
    
    /**
     * Ejecuta una transacción recurrente en modo PATTERN
     */
    private void executePatternRecurring(RecurringTransaction recurring, LocalDate today) {
        // Verificar que no haya expirado
        if (recurring.getEndDate() != null && today.isAfter(recurring.getEndDate())) {
            recurring.setActive(false);
            recurringRepository.save(recurring);
            logger.info("Transacción recurrente " + recurring.getId() + " expirada, marcada como inactiva");
            return;
        }
        
        // Verificar que no exista transacción para hoy (idempotencia)
        if (!transactionExists(recurring, today)) {
            // Crear transacción
            Transaction transaction = new Transaction();
            transaction.setUser(recurring.getUser());
            transaction.setAccount(recurring.getAccount());
            transaction.setCategory(recurring.getCategory());
            transaction.setAmount(recurring.getAmount());
            transaction.setType(recurring.getType());
            transaction.setDate(today);
            transaction.setNote("[Auto] " + recurringService.generateDescription(recurring));
            
            transactionRepository.save(transaction);
            logger.info("Transacción creada para recurrente PATTERN " + recurring.getId());
        }
        
        // Calcular próxima ejecución
        LocalDate nextDate = recurringService.computeNextRunDate(
                today.plusDays(1),
                recurring.getFrequency(),
                recurring.getInterval(),
                recurring.getDayOfMonth(),
                recurring.getDayOfWeek()
        );
        
        recurring.setNextRunDate(nextDate);
        recurringRepository.save(recurring);
        logger.info("Próxima ejecución de " + recurring.getId() + " calculada para " + nextDate);
    }
    
    /**
     * Ejecuta una transacción recurrente en modo MANUAL
     */
    private void executeManualRecurring(RecurringTransactionDate rtDate, LocalDate today) {
        RecurringTransaction recurring = rtDate.getRecurringTransaction();
        
        // Verificar que la recurrente esté activa
        if (!recurring.getActive()) {
            logger.info("Recurrente MANUAL " + recurring.getId() + " inactiva, saltando");
            return;
        }
        
        // Verificar que no exista transacción (idempotencia)
        if (!transactionExists(recurring, rtDate.getScheduledDate())) {
            // Crear transacción
            Transaction transaction = new Transaction();
            transaction.setUser(recurring.getUser());
            transaction.setAccount(recurring.getAccount());
            transaction.setCategory(recurring.getCategory());
            transaction.setAmount(recurring.getAmount());
            transaction.setType(recurring.getType());
            transaction.setDate(rtDate.getScheduledDate());
            transaction.setNote("[Auto] " + recurringService.generateDescription(recurring));
            
            transactionRepository.save(transaction);
            logger.info("Transacción creada para fecha " + rtDate.getScheduledDate() + " de recurrente MANUAL " + recurring.getId());
        }
        
        // Marcar como ejecutada
        rtDate.setExecuted(true);
        rtDate.setExecutedDate(today);
        dateRepository.save(rtDate);
        logger.info("Fecha " + rtDate.getScheduledDate() + " marcada como ejecutada");
    }
    
    /**
     * Verifica idempotencia: ¿existe una transacción para esta recurrencia en esta fecha?
     */
    private boolean transactionExists(RecurringTransaction recurring, LocalDate date) {
        return transactionRepository.existsByAccountAndCategoryAndAmountAndDate(
                recurring.getAccount(),
                recurring.getCategory(),
                recurring.getAmount(),
                date
        );
    }
}