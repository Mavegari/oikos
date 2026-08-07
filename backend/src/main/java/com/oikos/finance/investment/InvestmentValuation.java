package com.oikos.finance.investment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "investment_valuations")
public class InvestmentValuation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "investment_id", nullable = false)
    private Investment investment;
    
    @Column(nullable = false)
    private BigDecimal value;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructores
    public InvestmentValuation() {
        this.createdAt = Instant.now();
    }
    
    public InvestmentValuation(Investment investment, BigDecimal value, LocalDate date) {
        this.investment = investment;
        this.value = value;
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
    
    public Investment getInvestment() {
        return investment;
    }
    
    public void setInvestment(Investment investment) {
        this.investment = investment;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
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