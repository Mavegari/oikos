package com.oikos.finance.debt;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "debt_payments")
public class DebtPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructores
    public DebtPayment() {
        this.createdAt = Instant.now();
    }
    
    public DebtPayment(Debt debt, BigDecimal amount, LocalDate date) {
        this.debt = debt;
        this.amount = amount;
        this.date = date;
        this.createdAt = Instant.now();
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Debt getDebt() {
        return debt;
    }
    
    public void setDebt(Debt debt) {
        this.debt = debt;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
}