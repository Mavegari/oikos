package com.oikos.finance.investment;

import com.oikos.finance.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "investments")
public class Investment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal investedAmount;
    
    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvestmentValuation> valuations = new ArrayList<>();
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructores
    public Investment() {
        this.createdAt = Instant.now();
    }
    
    public Investment(User user, String name, BigDecimal investedAmount) {
        this.user = user;
        this.name = name;
        this.investedAmount = investedAmount;
        this.createdAt = Instant.now();
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getInvestedAmount() {
        return investedAmount;
    }
    
    public void setInvestedAmount(BigDecimal investedAmount) {
        this.investedAmount = investedAmount;
    }
    
    public List<InvestmentValuation> getValuations() {
        return valuations;
    }
    
    public void setValuations(List<InvestmentValuation> valuations) {
        this.valuations = valuations;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
}