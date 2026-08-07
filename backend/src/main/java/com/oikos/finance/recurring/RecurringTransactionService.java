package com.oikos.finance.recurring;

import com.oikos.finance.account.Account;
import com.oikos.finance.account.AccountRepository;
import com.oikos.finance.category.Category;
import com.oikos.finance.category.CategoryRepository;
import com.oikos.finance.recurring.dto.RecurringTransactionRequest;
import com.oikos.finance.recurring.dto.RecurringTransactionResponse;
import com.oikos.finance.recurring.dto.ScheduledDateDTO;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringTransactionService {
    
    private final RecurringTransactionRepository recurringRepository;
    private final RecurringTransactionDateRepository dateRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    
    public RecurringTransactionService(RecurringTransactionRepository recurringRepository,
                                       RecurringTransactionDateRepository dateRepository,
                                       AccountRepository accountRepository,
                                       CategoryRepository categoryRepository) {
        this.recurringRepository = recurringRepository;
        this.dateRepository = dateRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional
    public RecurringTransactionResponse create(RecurringTransactionRequest request, User user) {
        // Validaciones básicas
        validateRequest(request);
        
        // Obtener cuenta y categoría
        Account account = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        
        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        
        // Crear entidad
        RecurringTransaction recurring = new RecurringTransaction(user, account, category, request.amount(), request.type(), request.recurrenceMode());
        
        // Llenar campos según modo
        if (request.recurrenceMode() == RecurrenceMode.PATTERN) {
            recurring.setFrequency(request.frequency());
            recurring.setInterval(request.interval() != null ? request.interval() : 1);
            recurring.setDayOfMonth(request.dayOfMonth());
            recurring.setDayOfWeek(request.dayOfWeek());
            recurring.setStartDate(request.startDate());
            recurring.setEndDate(request.endDate());
            
            // Calcular nextRunDate
            LocalDate nextDate = computeNextRunDate(
                    request.startDate(),
                    request.frequency(),
                    request.interval() != null ? request.interval() : 1,
                    request.dayOfMonth(),
                    request.dayOfWeek()
            );
            recurring.setNextRunDate(nextDate);
        } else {
            // MANUAL mode: crear fechas específicas
            List<RecurringTransactionDate> dates = new ArrayList<>();
            for (LocalDate date : request.scheduledDates()) {
                dates.add(new RecurringTransactionDate(recurring, date));
            }
            recurring.setDates(dates);
        }
        
        RecurringTransaction saved = recurringRepository.save(recurring);
        return toResponse(saved);
    }
    
    public List<RecurringTransactionResponse> findAll(User user) {
        return recurringRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public RecurringTransactionResponse findById(UUID id, User user) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transacción recurrente no encontrada"));
        return toResponse(recurring);
    }
    
    @Transactional
    public RecurringTransactionResponse update(UUID id, RecurringTransactionRequest request, User user) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transacción recurrente no encontrada"));
        
        validateRequest(request);
        
        Account account = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        
        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        
        recurring.setAccount(account);
        recurring.setCategory(category);
        recurring.setAmount(request.amount());
        recurring.setType(request.type());
        recurring.setRecurrenceMode(request.recurrenceMode());
        
        if (request.recurrenceMode() == RecurrenceMode.PATTERN) {
            recurring.setFrequency(request.frequency());
            recurring.setInterval(request.interval() != null ? request.interval() : 1);
            recurring.setDayOfMonth(request.dayOfMonth());
            recurring.setDayOfWeek(request.dayOfWeek());
            recurring.setStartDate(request.startDate());
            recurring.setEndDate(request.endDate());
            
            LocalDate nextDate = computeNextRunDate(
                    request.startDate(),
                    request.frequency(),
                    request.interval() != null ? request.interval() : 1,
                    request.dayOfMonth(),
                    request.dayOfWeek()
            );
            recurring.setNextRunDate(nextDate);
            recurring.getDates().clear();
        } else {
            recurring.getDates().clear();
            List<RecurringTransactionDate> newDates = new ArrayList<>();
            for (LocalDate date : request.scheduledDates()) {
                newDates.add(new RecurringTransactionDate(recurring, date));
            }
            recurring.setDates(newDates);
        }
        
        RecurringTransaction updated = recurringRepository.save(recurring);
        return toResponse(updated);
    }
    
    @Transactional
    public void delete(UUID id, User user) {
        RecurringTransaction recurring = recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transacción recurrente no encontrada"));
        recurringRepository.delete(recurring);
    }
    
    /**
     * Calcula la próxima fecha de ejecución según el patrón
     */
    public LocalDate computeNextRunDate(LocalDate startDate, Frequency frequency, Integer interval, 
                                        Integer dayOfMonth, DayOfWeek dayOfWeek) {
        if (frequency == null) {
            throw new IllegalArgumentException("Frequency es requerido para modo PATTERN");
        }
        
        int safeInterval = interval != null ? interval : 1;
        
        switch (frequency) {
            case DAILY:
                return startDate.plusDays(safeInterval);
            
            case WEEKLY:
                if (dayOfWeek != null) {
                    LocalDate candidate = startDate;
                    // Buscar el próximo día de la semana
                    while (!candidate.getDayOfWeek().name().equals(dayOfWeek.name())) {
                        candidate = candidate.plusDays(1);
                    }
                    // Agregar intervalo en semanas
                    return candidate.plusWeeks(safeInterval - 1);
                }
                return startDate.plusWeeks(safeInterval);
            
            case MONTHLY:
                if (dayOfMonth == null) {
                    throw new IllegalArgumentException("dayOfMonth es requerido para MONTHLY");
                }
                LocalDate nextMonth = startDate.plusMonths(safeInterval);
                int lastDayOfMonth = nextMonth.lengthOfMonth();
                int day = Math.min(dayOfMonth, lastDayOfMonth);
                return nextMonth.withDayOfMonth(day);
            
            case YEARLY:
                if (dayOfMonth == null) {
                    throw new IllegalArgumentException("dayOfMonth es requerido para YEARLY");
                }
                LocalDate nextYear = startDate.plusYears(safeInterval);
                int lastDay = nextYear.lengthOfMonth();
                int dayToUse = Math.min(dayOfMonth, lastDay);
                return nextYear.withDayOfMonth(dayToUse);
            
            default:
                throw new IllegalArgumentException("Frequency desconocida: " + frequency);
        }
    }
    
    /**
     * Genera descripción legible de la recurrencia
     */
    public String generateDescription(RecurringTransaction rt) {
        if (rt.getRecurrenceMode() == RecurrenceMode.PATTERN) {
            return generatePatternDescription(rt);
        } else {
            return generateManualDescription(rt);
        }
    }
    
    private String generatePatternDescription(RecurringTransaction rt) {
        StringBuilder sb = new StringBuilder();
        
        switch (rt.getFrequency()) {
            case DAILY:
                if (rt.getInterval() == 1) {
                    sb.append("Diariamente");
                } else {
                    sb.append("Cada ").append(rt.getInterval()).append(" días");
                }
                break;
            
            case WEEKLY:
                if (rt.getDayOfWeek() != null) {
                    sb.append("Cada ").append(dayOfWeekToSpanish(rt.getDayOfWeek()));
                } else if (rt.getInterval() == 1) {
                    sb.append("Semanalmente");
                } else {
                    sb.append("Cada ").append(rt.getInterval()).append(" semanas");
                }
                break;
            
            case MONTHLY:
                if (rt.getInterval() == 1) {
                    sb.append("Cada mes el ").append(rt.getDayOfMonth());
                } else if (rt.getInterval() == 2) {
                    sb.append("Bimensual, día ").append(rt.getDayOfMonth());
                } else if (rt.getInterval() == 3) {
                    sb.append("Trimestral, día ").append(rt.getDayOfMonth());
                } else {
                    sb.append("Cada ").append(rt.getInterval()).append(" meses, día ").append(rt.getDayOfMonth());
                }
                break;
            
            case YEARLY:
                sb.append("Anualmente, el ").append(rt.getDayOfMonth());
                break;
        }
        
        if (rt.getEndDate() != null) {
            sb.append(" hasta ").append(rt.getEndDate());
        } else {
            sb.append(" indefinidamente");
        }
        
        return sb.toString();
    }
    
    private String generateManualDescription(RecurringTransaction rt) {
        List<RecurringTransactionDate> dates = rt.getDates();
        int count = dates.size();
        
        if (count == 0) {
            return "Sin fechas programadas";
        }
        
        StringBuilder sb = new StringBuilder(count).append(" cuota");
        if (count > 1) sb.append("s");
        sb.append(": ");
        
        // Mostrar primeras 3 fechas
        for (int i = 0; i < Math.min(3, count); i++) {
            if (i > 0) sb.append(", ");
            LocalDate date = dates.get(i).getScheduledDate();
            sb.append(date.getDayOfMonth()).append("/").append(date.getMonthValue());
        }
        
        if (count > 3) {
            sb.append(", ...");
        }
        
        return sb.toString();
    }
    
    private String dayOfWeekToSpanish(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "lunes";
            case TUESDAY -> "martes";
            case WEDNESDAY -> "miércoles";
            case THURSDAY -> "jueves";
            case FRIDAY -> "viernes";
            case SATURDAY -> "sábado";
            case SUNDAY -> "domingo";
        };
    }
    
    private void validateRequest(RecurringTransactionRequest request) {
        if (request.recurrenceMode() == RecurrenceMode.PATTERN) {
            if (request.frequency() == null) {
                throw new IllegalArgumentException("frequency es requerido para modo PATTERN");
            }
            if (request.startDate() == null) {
                throw new IllegalArgumentException("startDate es requerido para modo PATTERN");
            }
            if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
                throw new IllegalArgumentException("endDate debe ser >= startDate");
            }
            if ((request.frequency() == Frequency.MONTHLY || request.frequency() == Frequency.YEARLY) 
                    && request.dayOfMonth() == null) {
                throw new IllegalArgumentException("dayOfMonth es requerido para " + request.frequency());
            }
        } else {
            if (request.scheduledDates() == null || request.scheduledDates().isEmpty()) {
                throw new IllegalArgumentException("scheduledDates es requerido para modo MANUAL");
            }
        }
    }
    
    private RecurringTransactionResponse toResponse(RecurringTransaction rt) {
        List<ScheduledDateDTO> scheduledDates = null;
        Integer pendingPaymentsCount = null;
        
        if (rt.getRecurrenceMode() == RecurrenceMode.MANUAL) {
            scheduledDates = rt.getDates().stream()
                    .map(d -> new ScheduledDateDTO(d.getId(), d.getScheduledDate(), d.getExecuted(), d.getExecutedDate()))
                    .toList();
            pendingPaymentsCount = (int) rt.getDates().stream()
                    .filter(d -> !d.getExecuted())
                    .count();
        }
        
        Long daysUntilNextRun = null;
        if (rt.getNextRunDate() != null) {
            daysUntilNextRun = ChronoUnit.DAYS.between(LocalDate.now(), rt.getNextRunDate());
        }
        
        return new RecurringTransactionResponse(
                rt.getId(),
                rt.getAccount().getId(),
                rt.getAccount().getName(),
                rt.getCategory().getId(),
                rt.getCategory().getName(),
                rt.getAmount(),
                rt.getType(),
                rt.getRecurrenceMode(),
                rt.getFrequency(),
                rt.getInterval(),
                rt.getDayOfMonth(),
                rt.getDayOfWeek(),
                rt.getStartDate(),
                rt.getEndDate(),
                rt.getNextRunDate(),
                scheduledDates,
                pendingPaymentsCount,
                rt.getActive(),
                generateDescription(rt),
                daysUntilNextRun,
                rt.getCreatedAt()
        );
    }
}