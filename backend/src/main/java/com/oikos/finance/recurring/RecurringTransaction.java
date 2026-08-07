package com.oikos.finance.recurring;

import com.oikos.finance.account.Account;
import com.oikos.finance.category.Category;
import com.oikos.finance.transaction.TransactionType;
import com.oikos.finance.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME, EXPENSE
    
    // Modo
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurrenceMode recurrenceMode; // PATTERN, MANUAL
    
    // Solo para PATTERN mode
    @Enumerated(EnumType.STRING)
    private Frequency frequency; // DAILY, WEEKLY, MONTHLY, YEARLY
    
    private Integer interval; // default 1
    
    @Column(name = "day_of_month")
    private Integer dayOfMonth; // 1-31, null si no aplica
    
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY, ..., null si no aplica
    
    // Ciclo temporal
    private LocalDate startDate;
    
    private LocalDate endDate; // null = indefinido
    
    private LocalDate nextRunDate; // solo para PATTERN mode
    
    // Relación a fechas específicas (MANUAL mode)
    @OneToMany(mappedBy = "recurringTransaction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecurringTransactionDate> dates = new ArrayList<>();
    
    @Column(nullable = false)
    private Boolean active; // default TRUE
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Instant updatedAt;
    
    // Constructores
    public RecurringTransaction() {
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    public RecurringTransaction(User user, Account account, Category category, BigDecimal amount, 
                                TransactionType type, RecurrenceMode recurrenceMode) {
        this.user = user;
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.type = type;
        this.recurrenceMode = recurrenceMode;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public RecurrenceMode getRecurrenceMode() {
        return recurrenceMode;
    }
    
    public void setRecurrenceMode(RecurrenceMode recurrenceMode) {
        this.recurrenceMode = recurrenceMode;
    }
    
    public Frequency getFrequency() {
        return frequency;
    }
    
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
    
    public Integer getInterval() {
        return interval;
    }
    
    public void setInterval(Integer interval) {
        this.interval = interval;
    }
    
    public Integer getDayOfMonth() {
        return dayOfMonth;
    }
    
    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDate getNextRunDate() {
        return nextRunDate;
    }
    
    public void setNextRunDate(LocalDate nextRunDate) {
        this.nextRunDate = nextRunDate;
    }
    
    public List<RecurringTransactionDate> getDates() {
        return dates;
    }
    
    public void setDates(List<RecurringTransactionDate> dates) {
        this.dates = dates;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}