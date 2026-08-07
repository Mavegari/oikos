package com.oikos.finance.recurring;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recurring_transaction_dates")
public class RecurringTransactionDate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurring_transaction_id", nullable = false)
    private RecurringTransaction recurringTransaction;
    
    @Column(nullable = false)
    private LocalDate scheduledDate;
    
    @Column(nullable = false)
    private Boolean executed; // default FALSE
    
    private LocalDate executedDate;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructores
    public RecurringTransactionDate() {
        this.executed = false;
        this.createdAt = Instant.now();
    }
    
    public RecurringTransactionDate(RecurringTransaction recurringTransaction, LocalDate scheduledDate) {
        this.recurringTransaction = recurringTransaction;
        this.scheduledDate = scheduledDate;
        this.executed = false;
        this.createdAt = Instant.now();
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public RecurringTransaction getRecurringTransaction() {
        return recurringTransaction;
    }
    
    public void setRecurringTransaction(RecurringTransaction recurringTransaction) {
        this.recurringTransaction = recurringTransaction;
    }
    
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public Boolean getExecuted() {
        return executed;
    }
    
    public void setExecuted(Boolean executed) {
        this.executed = executed;
    }
    
    public LocalDate getExecutedDate() {
        return executedDate;
    }
    
    public void setExecutedDate(LocalDate executedDate) {
        this.executedDate = executedDate;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
}