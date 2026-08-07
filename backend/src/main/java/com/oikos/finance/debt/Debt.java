package com.oikos.finance.debt;

import com.oikos.finance.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "debts")
public class Debt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DebtType type; // I_OWE, OWED_TO_ME
    
    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DebtPayment> payments = new ArrayList<>();
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructores
    public Debt() {
        this.createdAt = Instant.now();
    }
    
    public Debt(User user, String name, BigDecimal totalAmount, DebtType type) {
        this.user = user;
        this.name = name;
        this.totalAmount = totalAmount;
        this.type = type;
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
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public DebtType getType() {
        return type;
    }
    
    public void setType(DebtType type) {
        this.type = type;
    }
    
    public List<DebtPayment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<DebtPayment> payments) {
        this.payments = payments;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
}